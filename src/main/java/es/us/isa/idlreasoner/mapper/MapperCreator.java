package es.us.isa.idlreasoner.mapper;

import es.us.isa.idlreasoner.util.Utils;

import java.io.IOException;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_AUX_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;
import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class MapperCreator {

    public static AbstractMapper createMapper(String specificationType, String idlPath, String apiSpecificationPath, String operationPath, String operationType) {
        AbstractMapper mapper = null;
		String compiler = readProperty("compiler");

		if(specificationType.toLowerCase().equals("oas") && compiler.toLowerCase().equals("minizinc"))
            mapper = new OAS2MiniZincMapper(apiSpecificationPath, operationPath, operationType);
		else
            terminate("Specification type " + specificationType + " and or compiler " + compiler + " not supported.");

		// DependenciesMapper
		if(compiler.toLowerCase().equals("minizinc"))
			mapper.dm = new DependenciesMapper("./"+ IDL_FILES_FOLDER + "/" + idlPath);
		else
            terminate("Compiler " + compiler + " not supported.");

        try {
            mapper.mapVariables();
        } catch (IOException e) {
            terminate("There was an error while mapping the variables for analysing dependencies. Try again.", e);
        }

        return mapper;
    }

    public static AbstractMapper createMapper(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
        AbstractMapper mapper = null;
        String compiler = readProperty("compiler");

		// Create IDL file from IDL4OAS document:
		if(specificationType.toLowerCase().equals("oas") && compiler.toLowerCase().equals("minizinc")) {
			try {
				OAS2MiniZincMapper.generateIDLfromIDL4OAS(apiSpecificationPath, operationPath, operationType);
			} catch (IOException e) {
                Utils.terminate("There was an error while creating the file containing dependencies. Try again.", e);
			}
            mapper = new OAS2MiniZincMapper(apiSpecificationPath, operationPath, operationType);
		} else
		    terminate("Specification type " + specificationType + " and or compiler " + compiler + " not supported.");

        // DependenciesMapper
        if(compiler.toLowerCase().equals("minizinc"))
            mapper.dm = new DependenciesMapper(IDL_AUX_FILE);
        else
            terminate("Compiler " + compiler + " not supported.");

        try {
            mapper.mapVariables();
        } catch (IOException e) {
            terminate("There was an error while mapping the variables for analysing dependencies. Try again.", e);
        }

        return mapper;
    }
}
