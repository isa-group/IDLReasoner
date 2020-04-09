package es.us.isa.idlreasoner.mapper;

import java.io.IOException;

import static es.us.isa.idlreasoner.util.FileManager.readFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class MapperCreator {

    public static AbstractMapper createMapper(String specificationType, String idlPath, String apiSpecificationPath, String operationPath, String operationType) {
        AbstractMapper mapper = null;

		if(specificationType.toLowerCase().equals("oas")) {
            if (idlPath == null) {
                OAS2MiniZincMapper.generateIDLfromIDL4OAS(apiSpecificationPath, operationPath, operationType);
            }
            mapper = new OAS2MiniZincMapper(apiSpecificationPath, operationPath, operationType);
        } else
            terminate("Specification type " + specificationType + " not supported.");

		// DependenciesMapper
        mapper.dm = new DependenciesMapper(idlPath==null ? IDL_AUX_FILE : "./" + IDL_FILES_FOLDER + "/" + idlPath);
        mapper.idlConstraints = readFile(BASE_CONSTRAINTS_FILE);

        try {
            mapper.mapVariables();
        } catch (IOException e) {
            terminate("There was an error while mapping the variables for analysing dependencies. Try again.", e);
        }

        return mapper;
    }
}
