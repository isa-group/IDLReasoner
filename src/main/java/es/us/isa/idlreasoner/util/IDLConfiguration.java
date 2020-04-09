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
    public static String STRING_INT_MAPPING_FILE = "./" + IDL_AUX_FOLDER + "/string_int_mapping.json";;
    public static String PARAMETER_NAMES_MAPPING_FILE = "./" + IDL_AUX_FOLDER + "/parameter_names_mapping.json";;
    public static String SOLVER;
    public static String IDL_FILES_FOLDER;
    public static String MAX_RESULTS;
    public static String BASE_CONSTRAINTS_FILE;
    public static String FULL_CONSTRAINTS_FILE;
    public static String DATA_FILE;
    public static String IDL_AUX_FILE;

    private static void updateConf() {
        SOLVER = readProperty("solver");
        IDL_FILES_FOLDER = readProperty("idlFolder");
        MAX_RESULTS = readProperty("maxResults");
        BASE_CONSTRAINTS_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("base_constraints_file");
        FULL_CONSTRAINTS_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("full_constraints_file");
        DATA_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("data_file");
        IDL_AUX_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("idl_aux_file");
    }

    public static void initFilesAndConf() {
        initConfigurationFile();
        updateConf();
        if (!(new File(STRING_INT_MAPPING_FILE)).exists()) {
            createFileIfNotExists(STRING_INT_MAPPING_FILE);
            appendContentToFile(STRING_INT_MAPPING_FILE, "{ }");
        }
        recreateFile(PARAMETER_NAMES_MAPPING_FILE);
        appendContentToFile(PARAMETER_NAMES_MAPPING_FILE, "{ }");
        recreateFile(IDL_AUX_FILE);
        recreateFile(BASE_CONSTRAINTS_FILE);
        recreateFile(DATA_FILE);
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
                bw.append("\n");
                bw.append("# The following files are under ./idl_aux_files\n");
                bw.append("base_constraints_file=base_constraints.mzn\n");
                bw.append("full_constraints_file=full_constraints.mzn\n");
                bw.append("data_file=data.dzn\n");
                bw.append("idl_aux_file=constraints.idl\n");

                bw.flush();
                bw.close();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
