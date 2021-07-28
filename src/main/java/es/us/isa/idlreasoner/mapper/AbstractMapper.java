package es.us.isa.idlreasoner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import es.us.isa.idlreasoner.util.CommonResources;
import org.apache.commons.lang3.RandomStringUtils;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.parseSpecParamName;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class AbstractMapper {

    CommonResources cr;

    String specificationPath;
//    private int MIN_STRING_INT_MAPPING = 10;
    final int MAX_STRING_INT_MAPPING = 100000;

    Map<String, Map.Entry<String, Boolean>> operationParameters; // <name, <type, required>>
    BiMap<String, String> parameterNamesMapping;
    BiMap<String, Integer> stringIntMapping;
    BiMap<String, Integer> stringIntMappingIDLValues; // This map contains ONLY the values found in the IDL. Doesn't ever change
    Integer stringToIntCounter;
    Integer stringToIntCounterIDLValues; // Equal to the number of values found in the IDL. Doesn't ever change
    Integer STRING_TO_INT_FIXED_COUNTER; // Counter up to which string-int entries should be preserved when resetting the bimap

    DependenciesMapper dm;

    // The following Strings represent different extracts of the MiniZinc problem:
    String variables = "";
    String variablesData = ""; // Data to instantiate model (data.dzn)
    String idlConstraints = "";
    String requiredVarsConstraints = "";
    String redundantSolutionsConstraints = ""; // Set of constraints to avoid redundant solutions when calling pseudoRandomRequest
    String baseProblem = "";
    String currentProblem = "";
    final String RANDOM_SEARCH = "include \"gecode.mzn\";\n" +
            "solve ::int_default_search(random, indomain_random) satisfy;\n";

    public AbstractMapper(CommonResources cr) {
        this.cr = cr;
        operationParameters = new HashMap<>();
        parameterNamesMapping = HashBiMap.create();
        stringIntMapping = HashBiMap.create();
        stringToIntCounter = 0;
    }

    public abstract void mapVariables() throws IOException;

    String origToChangedParamName(String origParamName) {
        String changedParamName = parameterNamesMapping.inverse().get(origParamName);
        if (changedParamName != null) {
            return changedParamName;
        } else {
            String parsedParamName = parseSpecParamName(origParamName);
            if (!parsedParamName.equals(origParamName)) {
                parameterNamesMapping.put(parsedParamName, origParamName);
                return parsedParamName;
            }
        }
        return origParamName;
    }

    private String changedToOrigParamName(String changedParamName) {
        String origParamName = parameterNamesMapping.get(changedParamName);
        if (origParamName != null) {
            return origParamName;
        }
        return changedParamName;
    }

    private String origToChangedParamValue(String parameter, String value) {
        Map.Entry<String, Boolean> paramFeatures = operationParameters.get(parameter);
        if (paramFeatures != null) {
            if (paramFeatures.getKey().equals("string") || paramFeatures.getKey().equals("array")) {
                Integer intMapping = stringIntMapping.get(value);
                if (intMapping != null) {
                    return Integer.toString(intMapping);
                } else {
                    while (stringIntMapping.inverse().get(stringToIntCounter) != null) stringToIntCounter++;
                    stringIntMapping.put(value, stringToIntCounter);
                    return Integer.toString(stringToIntCounter++);
                }
            } else if (paramFeatures.getKey().equals("number")) {
                return value.replaceAll("\\.\\d+", "");
            } else if (paramFeatures.getKey().equals("boolean")) {
                if (value.equals("true"))
                    return "1";
                else if (value.equals("false"))
                    return "0";
            }
        }

        return value;
    }

    private String changedToOrigParamValue(String parameter, String value) {
        Map.Entry<String, Boolean> paramFeatures = operationParameters.get(changedToOrigParamName(parameter));
        if (paramFeatures != null) {
            if (paramFeatures.getKey().equals("string") || paramFeatures.getKey().equals("array")) {
                String stringMapping;
                try {
                    stringMapping = stringIntMapping.inverse().get(new Integer(value));
                } catch (NumberFormatException e) {
                    return value;
                }
                if (stringMapping != null) {
                    return stringMapping;
                } else {
                    String newStringMapping;
                    do { newStringMapping = RandomStringUtils.randomAscii(ThreadLocalRandom.current().nextInt(1,10)); }
                    while (stringIntMapping.get(newStringMapping)!=null);
                    stringIntMapping.put(newStringMapping, new Integer(value));
                    return newStringMapping;
                }
            } else if (paramFeatures.getKey().equals("boolean")) {
                if (value.equals("1"))
                    return "true";
                else if (value.equals("0"))
                    return "false";
            }
        }

        return value;
    }

    public void setParamToValue(String parameter, String value) {
        currentProblem += "constraint " + origToChangedParamName(parameter) + " = " + origToChangedParamValue(parameter, value) + ";\n";
    }

    public void setParamToValue(String changedParamName, String origParamName, String value) {
        currentProblem += "constraint " + origToChangedParamName(changedParamName) + " = " + origToChangedParamValue(origParamName, value) + ";\n";
    }

    public void appendConstraintsRedundantSolutions() {
        currentProblem += redundantSolutionsConstraints;
    }

    void mapRedundantConstraint(String changedParamName, String paramValue) {
        redundantSolutionsConstraints += "constraint ((" + changedParamName + "Set==0) -> (" + changedParamName + "==" + paramValue + "));\n";
    }

    void mapRequiredVar(String changedParamName) {
        requiredVarsConstraints += "constraint " + changedParamName + "Set = 1;\n";
    }

    public void inverseConstraints() {
        currentProblem = variables
                + "\n" + idlConstraints.replaceAll("^constraint", "constraint not")
                + "\n" + requiredVarsConstraints;
    }

    public void finishConstraintsFile() {
        currentProblem += "solve satisfy;\n";
        writeContentToFile(cr.BASE_CONSTRAINTS_FILE, currentProblem);
    }

    public void finishConstraintsFileWithSearch() {
        currentProblem += RANDOM_SEARCH;
        writeContentToFile(cr.BASE_CONSTRAINTS_FILE, currentProblem);
    }

    public void resetStringIntMapping() {
        stringIntMapping = HashBiMap.create(stringIntMapping.entrySet().stream()
                .filter(entry -> entry.getValue() < STRING_TO_INT_FIXED_COUNTER)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        stringToIntCounter = STRING_TO_INT_FIXED_COUNTER;
    }

    public void fixStringToIntCounter() {
        STRING_TO_INT_FIXED_COUNTER = stringToIntCounter;
    }

    public Set<String> getOperationParameters() {
		return operationParameters.keySet();
	}

    public Boolean isOptionalParameter(String paramName) {
        Map.Entry<String, Boolean> paramFeatures = operationParameters.get(paramName);
        if (paramFeatures != null) {
            return !paramFeatures.getValue();
        } else {
            return null;
        }
    }

    public void initializeStringIntMappingWithIDLValues() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Integer>> typeRef = new TypeReference<HashMap<String, Integer>>() {};
        stringIntMapping = HashBiMap.create(mapper.readValue(new File(cr.STRING_INT_MAPPING_FILE), typeRef));
        stringIntMappingIDLValues = HashBiMap.create(stringIntMapping);
        Map.Entry<String, Integer> entryWithHighestInt = stringIntMapping.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);
        if (entryWithHighestInt != null) {
            stringToIntCounter = entryWithHighestInt.getValue()+1;
        } else {
            stringToIntCounter = 0;
        }
        stringToIntCounterIDLValues = stringToIntCounter;
        fixStringToIntCounter();
    }

    public void resetStringIntMappingWithIDLValues() {
        stringIntMapping = HashBiMap.create(stringIntMappingIDLValues);
        stringToIntCounter = stringToIntCounterIDLValues;
        fixStringToIntCounter();
    }

    void exportStringIntMappingToFile() throws IOException {
        recreateFile(cr.STRING_INT_MAPPING_FILE);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stringIntMapping);
        appendContentToFile(cr.STRING_INT_MAPPING_FILE, json);
    }

    public Map<String,String> setUpRequest(Map<String,String> mznSolution) {
        if (mznSolution == null)
            return null;
        Map<String,String> request = new HashMap<>();
        Iterator<Map.Entry<String, String>> cspVariables = mznSolution.entrySet().iterator();
        Map.Entry<String, String> currentCspVariable;

        while (cspVariables.hasNext()) {
            currentCspVariable = cspVariables.next();
            if (mznSolution.get(currentCspVariable.getKey() + "Set") != null)
                if (mznSolution.get(currentCspVariable.getKey() + "Set").equals("1"))
                    request.put(changedToOrigParamName(currentCspVariable.getKey()), changedToOrigParamValue(currentCspVariable.getKey(), currentCspVariable.getValue()));
        }

        return request;
    }

    public void updateDataFile(Map<String, List<String>> data) {
        StringBuilder newVariablesData = new StringBuilder();

        for (Map.Entry<String, Map.Entry<String, Boolean>> operationParameter: operationParameters.entrySet()) {
            List<String> paramValues = data.get(operationParameter.getKey());
            String changedParamName = origToChangedParamName(operationParameter.getKey());
            StringBuilder varData = new StringBuilder("data_" + changedParamName + " = {");
            if (paramValues != null && !paramValues.isEmpty()) {
                for (String paramValue: paramValues)
                    varData.append(origToChangedParamValue(operationParameter.getKey(), paramValue)).append(", ");
                varData = new StringBuilder(varData.substring(0, varData.length() - 2)); // trim last comma and space
                varData.append("};\n" + "data_").append(changedParamName).append("Set = {0, 1};\n");
            } else {
                varData.append("0};\n" + "data_").append(changedParamName).append("Set = {0};\n");
            }
            newVariablesData.append(varData);
        }

        variablesData = newVariablesData.toString();
        appendContentToFile(cr.DATA_FILE, variablesData);
    }


    public void resetCurrentProblem() {
        currentProblem = baseProblem;
    }

    public Boolean hasDeps() {
        return !idlConstraints.equals("");
    }
}
