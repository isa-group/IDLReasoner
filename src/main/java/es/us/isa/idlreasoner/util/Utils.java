package es.us.isa.idlreasoner.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static es.us.isa.idlreasoner.util.FileManager.openReader;
import static es.us.isa.idlreasoner.util.IDLConfiguration.BASE_CONSTRAINTS_FILE;
import static es.us.isa.interparamdep.generator.ReservedWords.RESERVED_WORDS;

public class Utils {

    public static String parseSpecParamName(String swaggerParamName) {
        String parsedParamName = swaggerParamName.replaceAll("[\\.\\-\\/\\:\\[\\]]", "_");
        if (RESERVED_WORDS.contains(parsedParamName))
            parsedParamName += "_R";
        return parsedParamName;
    }

    public static List<String> savePreviousBaseConstraintsFileContent() throws IOException {
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

    public static void terminate(String message, Exception e) {
        System.err.println(message);
        if (e != null)
            e.printStackTrace();
        System.exit(-1);
    }

    public static void terminate(String message) {
        System.err.println(message);
        System.exit(-1);
    }
}
