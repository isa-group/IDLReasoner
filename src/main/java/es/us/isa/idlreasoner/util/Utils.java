package es.us.isa.idlreasoner.util;

import static es.us.isa.interparamdep.generator.ReservedWords.RESERVED_WORDS;

public class Utils {

    public static String parseSpecParamName(String swaggerParamName) {
        String parsedParamName = swaggerParamName.replaceAll("[\\.\\-\\/\\:\\[\\]]", "_");
        if (RESERVED_WORDS.contains(parsedParamName))
            parsedParamName += "_R";
        return parsedParamName;
    }
}
