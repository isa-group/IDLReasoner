package es.us.isa.idlreasoner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.RandomStringUtils;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.PARAMETER_NAMES_MAPPING_FILE;
import static es.us.isa.idlreasoner.util.Utils.parseSpecParamName;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class AbstractMapper {

    String specificationPath;

    private final int MIN_STRING_INT_MAPPING = 1000;
    final int MAX_STRING_INT_MAPPING = 1000000;

    Map<String, Map.Entry<String, Boolean>> operationParameters; // <name, <type, required>>
    BiMap<String, String> parameterNamesMapping;
    BiMap<String, Integer> stringIntMapping;
    Integer stringToIntCounter;

    DependenciesMapper dm;

    String constraintsRedundantSolutions = ""; // Set of constraints to avoid redundant solutions when calling randomRequest

    public AbstractMapper() {
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
                    int randomInt;
                    do { randomInt = ThreadLocalRandom.current().nextInt(MIN_STRING_INT_MAPPING, MAX_STRING_INT_MAPPING); }
                    while (stringIntMapping.inverse().get(randomInt)!=null);
                    stringIntMapping.put(value, randomInt);
                    return Integer.toString(randomInt);
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
        Map.Entry<String, Boolean> paramFeatures = operationParameters.get(parameter);
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
        appendContentToFile(FULL_CONSTRAINTS_FILE, "constraint " + origToChangedParamName(parameter) + " = " + origToChangedParamValue(parameter, value) + ";\n");
    }

    public void setParamToValue(String changedParamName, String origParamName, String value) {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "constraint " + origToChangedParamName(changedParamName) + " = " + origToChangedParamValue(origParamName, value) + ";\n");
    }

    public void appendConstraintsRedundantSolutions() {
        appendContentToFile(FULL_CONSTRAINTS_FILE, constraintsRedundantSolutions);
    }

    void mapPSetZero(String paramName, String paramValue) {
        constraintsRedundantSolutions += "constraint ((" + origToChangedParamName(paramName) + "Set==0) -> (" + origToChangedParamName(paramName) + "==" + paramValue + "));\n";
    }

    public void finishConstraintsFile() {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "solve satisfy;\n");
    }

    public void resetStringIntMapping() {
        stringIntMapping = HashBiMap.create(stringIntMapping.entrySet().stream()
                .filter(entry -> entry.getValue() < stringToIntCounter)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
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

    void initializeParameterNamesMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        parameterNamesMapping = HashBiMap.create(mapper.readValue(new File(PARAMETER_NAMES_MAPPING_FILE), typeRef));
    }

    void initializeStringIntMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Integer>> typeRef = new TypeReference<HashMap<String, Integer>>() {};
        stringIntMapping = HashBiMap.create(mapper.readValue(new File(STRING_INT_MAPPING_FILE), typeRef));
        Map.Entry<String, Integer> entryWithHighestInt = stringIntMapping.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);
        if (entryWithHighestInt != null) {
            stringToIntCounter = entryWithHighestInt.getValue()+1;
        } else {
            stringToIntCounter = 0;
        }
    }

    void exportStringIntMappingToFile() throws IOException {
        recreateFile(STRING_INT_MAPPING_FILE);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stringIntMapping);
        appendContentToFile(STRING_INT_MAPPING_FILE, json);
    }

    void exportParameterNamesMappingToFile() throws IOException {
        recreateFile(PARAMETER_NAMES_MAPPING_FILE);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parameterNamesMapping);
        appendContentToFile(PARAMETER_NAMES_MAPPING_FILE, json);
    }

    public Map<String,String> setUpRequest(Map<String,String> mznSolution) {
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
}
