package es.us.isa.idlreasoner.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static es.us.isa.idlreasoner.util.IDLConfiguration.CONSTRAINTS_FILE;

public abstract class AbstractVariableMapper extends AbstractMapper {

    String apiSpecificationPath;

    abstract public void mapVariables() throws IOException;

    List<String> savePreviousFileContent() throws IOException {
        List<String> previousContent = new ArrayList<>();

        File file = new File(CONSTRAINTS_FILE);
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);

        String line = reader.readLine();
        while(line!=null) {
            previousContent.add(line);
            line = reader.readLine();
        }

        return previousContent;
    }
}
