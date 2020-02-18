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
    public static String COMPILER;
    public static String SOLVER;
    public static String IDL_FILES_FOLDER;
    public static String MAX_RESULTS;
    public static String BASE_CONSTRAINTS_FILE;
    public static String FULL_CONSTRAINTS_FILE;
    public static String IDL_AUX_FILE;
    public static String STRING_INT_MAPPING_FILE;
    public static String PARAMETER_NAMES_MAPPING_FILE;

    private static void updateConf() {
        COMPILER = readProperty("compiler");
        SOLVER = readProperty("solver");
        IDL_FILES_FOLDER = readProperty("fileRoute");
        MAX_RESULTS = readProperty("maxResults");
        BASE_CONSTRAINTS_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("base_constraints_file");
        FULL_CONSTRAINTS_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("full_constraints_file");
        IDL_AUX_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("idl_aux_file");
        STRING_INT_MAPPING_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("string_int_mapping_file");
        PARAMETER_NAMES_MAPPING_FILE = "./" + IDL_AUX_FOLDER + "/" + readProperty("parameter_names_mapping_file");
    }

    public static void initFilesAndConf() {
        initConfigurationFile();
        updateConf();
        if (!(new File(STRING_INT_MAPPING_FILE)).exists()) {
            createFileIfNotExists(STRING_INT_MAPPING_FILE);
            appendContentToFile(STRING_INT_MAPPING_FILE, "{ }");
        }
        if (!(new File(PARAMETER_NAMES_MAPPING_FILE)).exists()) {
            createFileIfNotExists(PARAMETER_NAMES_MAPPING_FILE);
            appendContentToFile(PARAMETER_NAMES_MAPPING_FILE, "{ }");
        }
        recreateFile(IDL_AUX_FILE);
        recreateFile(BASE_CONSTRAINTS_FILE);
    }

    private static void initConfigurationFile() {
        String filePath = "src/main/resources/idl-reasoner.properties";
        createFileIfNotExists(filePath);
        BufferedReader br = openReader(filePath);

        try {
            if(br.readLine()==null) {
                BufferedWriter bw = openWriter(filePath);

                bw.append("compiler=Minizinc\n");
                bw.append("solver=Chuffed\n");
                bw.append("fileRoute=src/test/resources\n");
                bw.append("maxResults=100\n");
                bw.append("\n");
                bw.append("base_constraints_file=base_constraints.mzn\n");
                bw.append("full_constraints_file=full_constraints.mzn\n");
                bw.append("idl_aux_file=constraints.idl\n");
                bw.append("\n");
                bw.append("# DO NOT CHANGE THE FOLLOWING 2 VARIABLES!!!\n");
                bw.append("string_int_mapping_file=string_int_mapping.json\n");
                bw.append("parameter_names_mapping_file=parameter_names_mapping.json\n");

                bw.flush();
                bw.close();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
