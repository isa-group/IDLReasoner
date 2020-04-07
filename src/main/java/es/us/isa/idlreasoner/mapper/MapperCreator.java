package es.us.isa.idlreasoner.mapper;

import java.io.IOException;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_AUX_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class MapperCreator {

    public static AbstractMapper createMapper(String specificationType, String idlPath, String apiSpecificationPath, String operationPath, String operationType) {
        AbstractMapper mapper = null;

		if(specificationType.toLowerCase().equals("oas"))
            mapper = new OAS2MiniZincMapper(apiSpecificationPath, operationPath, operationType);
		else
            terminate("Specification type " + specificationType + " not supported.");

		// DependenciesMapper
        mapper.dm = new DependenciesMapper(idlPath==null ? IDL_AUX_FILE : "./"+ IDL_FILES_FOLDER + "/" + idlPath);

        try {
            mapper.mapVariables();
        } catch (IOException e) {
            terminate("There was an error while mapping the variables for analysing dependencies. Try again.", e);
        }

        return mapper;
    }

    public static AbstractMapper createMapper(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
		// Create IDL file from IDL4OAS document:
		if(specificationType.toLowerCase().equals("oas")) {
			try {
				OAS2MiniZincMapper.generateIDLfromIDL4OAS(apiSpecificationPath, operationPath, operationType);
			} catch (IOException e) {
                terminate("There was an error while creating the file containing dependencies. Try again.", e);
			}
		} else
		    terminate("Specification type " + specificationType + " not supported.");

		return createMapper(specificationType, null, apiSpecificationPath, operationPath, operationType);
    }
}
