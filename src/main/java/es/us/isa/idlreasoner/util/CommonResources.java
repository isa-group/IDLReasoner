package es.us.isa.idlreasoner.util;

import java.util.Date;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_AUX_FOLDER;

public class CommonResources {

    public String BASE_CONSTRAINTS_FILE;
    public String DATA_FILE;
    public String BASE_DATA_FILE;
    public String IDL_AUX_FILE;
    public String STRING_INT_MAPPING_FILE;
    public String IDL_JSON_FILE;

    public CommonResources() {
        String folderName = Long.toString(new Date().getTime());
        BASE_CONSTRAINTS_FILE = "./" + IDL_AUX_FOLDER + "/" + folderName + "/base_constraints.mzn";
        BASE_DATA_FILE = "./" + IDL_AUX_FOLDER + "/" + folderName + "/base_data.dzn";
        DATA_FILE = "./" + IDL_AUX_FOLDER + "/" + folderName + "/data.dzn";
        IDL_AUX_FILE = "./" + IDL_AUX_FOLDER + "/" + folderName + "/constraints.idl";
        STRING_INT_MAPPING_FILE = "./" + IDL_AUX_FOLDER + "/" + folderName + "/string_int_mapping.json";
        IDL_JSON_FILE = "./" + IDL_AUX_FOLDER + "/" + folderName + "/idl.json";
    }
}
