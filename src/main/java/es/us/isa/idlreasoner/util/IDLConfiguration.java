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
//    public static String STRING_INT_MAPPING_FILE = "./" + IDL_AUX_FOLDER + "/string_int_mapping.json";;
    public static String SOLVER;
    public static String IDL_FILES_FOLDER;
    public static String MAX_RESULTS;
    public static Long TIMEOUT;

    private static void updateConf() {
        SOLVER = readProperty("solver");
        IDL_FILES_FOLDER = readProperty("idlFolder");
        MAX_RESULTS = readProperty("maxResults");
        TIMEOUT = Long.parseLong(readProperty("timeout"));
//        BASE_CONSTRAINTS_FILE = "./" + IDL_AUX_FOLDER + "/base_constraints.mzn";
//        DATA_FILE = "./" + IDL_AUX_FOLDER + "/data.dzn";
//        IDL_AUX_FILE = "./" + IDL_AUX_FOLDER + "/constraints.idl";
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
        recreateFile(cr.DATA_FILE);
    }

    private static void initConfigurationFile() {
        String filePath = "src/main/resources/idl-reasoner.properties";
        createFileIfNotExists(filePath);
        BufferedReader br = openReader(filePath);

        try {
            if(br.readLine()==null) {
                BufferedWriter bw = openWriter(filePath);

                bw.append("solver=Gecode\n");
                bw.append("idlFolder=src/test/resources\n");
                bw.append("maxResults=100\n");
                bw.append("timeout=1000\n");
                bw.append("\n");
//                bw.append("# The following files are under ./idl_aux_files\n");
//                bw.append("base_constraints_file=base_constraints.mzn\n");
//                bw.append("data_file=data.dzn\n");
//                bw.append("idl_aux_file=constraints.idl\n");

                bw.flush();
                bw.close();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
