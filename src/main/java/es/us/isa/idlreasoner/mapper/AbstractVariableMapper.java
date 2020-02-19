package es.us.isa.idlreasoner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBiMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public abstract class AbstractVariableMapper extends AbstractMapper {

    String constraintsRedundantSolutions = ""; // Set of constraints to avoid redundant solutions when calling randomRequest

    public AbstractVariableMapper(MapperResources mr) {
        super(mr);
    }

    abstract public void mapVariables() throws IOException;

    public void appendConstraintsRedundantSolutions() {
        appendContentToFile(FULL_CONSTRAINTS_FILE, constraintsRedundantSolutions);
    }

    List<String> savePreviousBaseConstraintsFileContent() throws IOException {
        List<String> previousContent = new ArrayList<>();
        BufferedReader reader = openReader(BASE_CONSTRAINTS_FILE);

        String line = reader.readLine();
        while(line!=null) {
            previousContent.add(line);
            line = reader.readLine();
        }
        reader.close();

        return previousContent;
    }

    void initializeParameterNamesMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        mr.parameterNamesMapping = HashBiMap.create(mapper.readValue(new File(PARAMETER_NAMES_MAPPING_FILE), typeRef));
    }

    void initializeStringIntMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Integer>> typeRef = new TypeReference<HashMap<String, Integer>>() {};
        mr.stringIntMapping = HashBiMap.create(mapper.readValue(new File(STRING_INT_MAPPING_FILE), typeRef));
        Map.Entry<String, Integer> entryWithHighestInt = mr.stringIntMapping.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);
        if (entryWithHighestInt != null) {
            mr.stringToIntCounter = entryWithHighestInt.getValue()+1;
        } else {
            mr.stringToIntCounter = 0;
        }
    }

    void exportStringIntMappingToFile() throws IOException {
        recreateFile(STRING_INT_MAPPING_FILE);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mr.stringIntMapping);
        appendContentToFile(STRING_INT_MAPPING_FILE, json);
    }

    void exportParameterNamesMappingToFile() throws IOException {
        recreateFile(PARAMETER_NAMES_MAPPING_FILE);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mr.parameterNamesMapping);
        appendContentToFile(PARAMETER_NAMES_MAPPING_FILE, json);
    }

    Map<String,String> setUpRequest(Map<String,String> mznSolution) {
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
