package es.us.isa.idlreasoner.util;

import static es.us.isa.interparamdep.generator.ReservedWords.RESERVED_WORDS;

public class Utils {

    public static String parseParamName(String paramName) {
        String parsedParamName = paramName.replaceAll("[\\[\\]]", "").replaceAll("[\\.\\-\\/\\:]", "_");
        if (RESERVED_WORDS.contains(parsedParamName))
            parsedParamName += "_R";
        return parsedParamName;
    }
}
