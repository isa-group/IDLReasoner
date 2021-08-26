package es.us.isa.idlreasoner.mapper;

import static es.us.isa.idlreasoner.util.Utils.terminate;

public class CALETAVariableMapperCreator {
	
	public static CALETAVariableMapper createMapper(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
		CALETAVariableMapper res = null;
			
		if(specificationType.toLowerCase().equals("oas")) {
			res = new OAS2CALETAVariableMapper(apiSpecificationPath, operationPath, operationType);
		}else {
	       terminate("Specification type " + specificationType + " not supported.");
		}

		return res;
		
	}
	
	
}
