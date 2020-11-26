package es.us.isa.idlreasoner.util;

import org.apache.commons.lang3.SystemUtils;

import static es.us.isa.idl.generator.ReservedWords.RESERVED_WORDS;

public class Utils {

    public static String parseSpecParamName(String swaggerParamName) {
        String parsedParamName = swaggerParamName.replaceAll("[\\.\\-\\/\\:\\[\\]]", "_");
        if (RESERVED_WORDS.contains(parsedParamName))
            parsedParamName += "_R";
        return parsedParamName;
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
