package es.us.isa.idlreasoner.mapper;

import es.us.isa.idlreasoner.pojos.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static es.us.isa.idlreasoner.util.FileManager.openReader;
import static es.us.isa.idlreasoner.util.IDLConfiguration.BASE_CONSTRAINTS_FILE;

public abstract class AbstractVariableMapper extends AbstractMapper {

    String apiSpecificationPath;
    Set<Variable> variables;
    Map<String, String> parameterNamesMapping;
    Map<String, Integer> stringIntMapping;

    public AbstractVariableMapper() {
        variables = new HashSet<>();
    }

    public Map<String, String> getParameterNamesMapping() {
        return parameterNamesMapping;
    }

    public void setParameterNamesMapping(Map<String, String> parameterNamesMapping) {
        this.parameterNamesMapping = parameterNamesMapping;
    }

    public Map<String, Integer> getStringIntMapping() {
        return stringIntMapping;
    }

    public void setStringIntMapping(Map<String, Integer> stringIntMapping) {
        this.stringIntMapping = stringIntMapping;
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    public void setVariables(Set<Variable> variables) {
        this.variables = variables;
    }

    abstract public void mapVariables() throws IOException;

    abstract public Map<String, Map<String,Integer>> getMappingParameters();

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

    String changeIfReservedWord(String word) {
        String changedWord = word;
        if(reservedWords.contains(word)) {
            changedWord += "_R";
            parameterNamesMapping.putIfAbsent(changedWord, word);
        }
        return changedWord;

    }
}
