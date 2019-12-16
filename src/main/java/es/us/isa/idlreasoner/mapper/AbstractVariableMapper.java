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

    public AbstractVariableMapper() {
        variables = new HashSet<>();
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
}
