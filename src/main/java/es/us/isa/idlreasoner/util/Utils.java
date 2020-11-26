package es.us.isa.idlreasoner.util;

import org.apache.commons.lang3.SystemUtils;

import static es.us.isa.interparamdep.generator.ReservedWords.RESERVED_WORDS;

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

    public static String[] getCommandProcessArgs() {
        String[] commandProcessArgs = new String[2];
        if (SystemUtils.IS_OS_WINDOWS) {
            commandProcessArgs[0] = "cmd.exe";
            commandProcessArgs[1] = "/c";
        }
        else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            commandProcessArgs[0] = "/bin/bash";
            commandProcessArgs[1] = "-c";
        }
        else
            terminate("Operating system " + System.getProperty("os.name") + " not supported.");

        return commandProcessArgs;
    }
}
