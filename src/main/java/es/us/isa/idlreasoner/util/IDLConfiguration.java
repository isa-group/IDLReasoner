package es.us.isa.idlreasoner.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public class IDLConfiguration {
    public final static String SOLUTION_SEP = "----------";
    public final static String IDL_AUX_FOLDER = "idl_aux_files";
    public static String SOLVER;
    public static String IDL_FILES_FOLDER;
    public static String MAX_RESULTS;
    public static Long TIMEOUT;
    public static String ANALYZER;

    private static void updateConf() {
        SOLVER = readProperty("solver");
        IDL_FILES_FOLDER = readProperty("idlFolder");
        MAX_RESULTS = readProperty("maxResults");
        TIMEOUT = Long.parseLong(readProperty("timeout"));
        ANALYZER = readProperty("analyzer");
    }
    
    public static void initJosnFile(CommonResources cr) {
    	 initConfigurationFile();
         updateConf();
         if (!(new File(cr.STRING_INT_MAPPING_FILE)).exists()) {
             createFileIfNotExists(cr.STRING_INT_MAPPING_FILE);
             appendContentToFile(cr.STRING_INT_MAPPING_FILE, "{ }");
         }
        recreateFile(cr.IDL_JSON_FILE);
    }

    public static void initFilesAndConf(CommonResources cr) {
        initConfigurationFile();
        updateConf();
        if (!(new File(cr.STRING_INT_MAPPING_FILE)).exists()) {
            createFileIfNotExists(cr.STRING_INT_MAPPING_FILE);
            appendContentToFile(cr.STRING_INT_MAPPING_FILE, "{ }");
        }
        recreateFile(cr.IDL_AUX_FILE);
        recreateFile(cr.BASE_CONSTRAINTS_FILE);
        recreateFile(cr.BASE_DATA_FILE);
        recreateFile(cr.DATA_FILE);
        recreateFile(cr.DATA_FILE);
        recreateFile(cr.IDL_JSON_FILE);
    }

    private static void initConfigurationFile() {
        String filePath = "src/main/resources/idl-reasoner.properties";
        createFileIfNotExists(filePath);
        BufferedReader br = openReader(filePath);

        try {
            if(br.readLine()==null) {
                BufferedWriter bw = openWriter(filePath);

                bw.append("solver=z3\n");
                bw.append("idlFolder=src/test/resources\n");
                bw.append("maxResults=100\n");
                bw.append("timeout=1000\n");
                bw.append("analyzer=Caleta\n");
                bw.append("\n");

                bw.flush();
                bw.close();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
