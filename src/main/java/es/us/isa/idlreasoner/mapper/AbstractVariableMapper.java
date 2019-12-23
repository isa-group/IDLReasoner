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
import static es.us.isa.idlreasoner.util.IDLConfiguration.PARAMETER_NAMES_MAPPING_FILE;

public abstract class AbstractVariableMapper extends AbstractMapper {

    public AbstractVariableMapper(MapperResources mr) {
        super(mr);
    }

//    Set<Variable> variables;
//    BiMap<String, String> parameterNamesMapping;
//    BiMap<String, Integer> stringIntMapping;

//    public AbstractVariableMapper() {
//        variables = new HashSet<>();
//    }
//
//    public BiMap<String, String> getParameterNamesMapping() {
//        return parameterNamesMapping;
//    }
//
//    public void setParameterNamesMapping(BiMap<String, String> parameterNamesMapping) {
//        this.parameterNamesMapping = parameterNamesMapping;
//    }
//
//    public BiMap<String, Integer> getStringIntMapping() {
//        return stringIntMapping;
//    }
//
//    public void setStringIntMapping(BiMap<String, Integer> stringIntMapping) {
//        this.stringIntMapping = stringIntMapping;
//    }
//
//    public Set<Variable> getVariables() {
//        return variables;
//    }
//
//    public void setVariables(Set<Variable> variables) {
//        this.variables = variables;
//    }

    abstract public void mapVariables() throws IOException;

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
}
