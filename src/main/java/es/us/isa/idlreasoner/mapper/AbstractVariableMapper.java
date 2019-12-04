package es.us.isa.idlreasoner.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static es.us.isa.idlreasoner.util.FileManager.openReader;
import static es.us.isa.idlreasoner.util.IDLConfiguration.CONSTRAINTS_FILE;

public abstract class AbstractVariableMapper extends AbstractMapper {

    String apiSpecificationPath;

    abstract public void mapVariables() throws IOException;

    List<String> savePreviousFileContent() throws IOException {
        List<String> previousContent = new ArrayList<>();
        BufferedReader reader = openReader(CONSTRAINTS_FILE);

        String line = reader.readLine();
        while(line!=null) {
            previousContent.add(line);
            line = reader.readLine();
        }
        reader.close();

        return previousContent;
    }
}
