package es.us.isa.idlreasoner.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public class IDLConfiguration {
    public static String COMPILER;
    public static String SOLVER;
    public static String IDL_FILES_FOLDER;
    public static String MAX_RESULTS;
    public static String CONSTRAINTS_FILE;

    public static void updateConf() {
        CONSTRAINTS_FILE = "./" + readProperty("aux_files_folder") + "/" + readProperty("constraints_file");

        InputStream inputStream;
        try {
            Properties props = new Properties();
            inputStream = new FileInputStream("./" + readProperty("aux_files_folder") + "/config.properties");
            props.load(inputStream);
            COMPILER = props.getProperty("compiler");
            SOLVER= props.getProperty("solver");
            IDL_FILES_FOLDER = props.getProperty("fileRoute");
            MAX_RESULTS = props.getProperty("maxResults");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }
}
