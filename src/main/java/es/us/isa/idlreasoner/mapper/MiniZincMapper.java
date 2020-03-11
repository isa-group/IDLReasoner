package es.us.isa.idlreasoner.mapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_AUX_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;
import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public class MiniZincMapper {

	private MiniZincConstraintMapper cm;
	private AbstractVariableMapper vm;
	
	public MiniZincMapper(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		String compiler = readProperty("compiler");

		// ConstraintMapper: must be created BEFORE the VariableMapper
		if(compiler.toLowerCase().equals("minizinc"))
			this.cm = new MiniZincConstraintMapper("./"+ IDL_FILES_FOLDER + "/" + idl, null);
		else {
			System.err.println("Compiler " + compiler + " not supported.");
			System.exit(-1);
		}

		// VariableMapper: must be created AFTER the ConstraintMapper
		if(specificationType.toLowerCase().equals("oas") && compiler.toLowerCase().equals("minizinc"))
			this.vm = new OAS2MiniZincVariableMapper(apiSpecificationPath, operationPath, operationType, cm.mr);
		else {
			System.err.println("Specification type " + specificationType + " and or compiler " + compiler + " not supported.");
			System.exit(-1);
		}

	}

	public MiniZincMapper(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
		String compiler = readProperty("compiler");

		// Create IDL file from IDL4OAS document:
		if(specificationType.toLowerCase().equals("oas") && compiler.toLowerCase().equals("minizinc")) {
			try {
				OAS2MiniZincVariableMapper.generateIDLfromIDL4OAS(apiSpecificationPath, operationPath, operationType);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Specification type " + specificationType + " and or compiler " + compiler + " not supported.");
			System.exit(-1);
		}

		// ConstraintMapper: must be created BEFORE the VariableMapper
		if(compiler.toLowerCase().equals("minizinc"))
			this.cm = new MiniZincConstraintMapper(IDL_AUX_FILE, null);
		else {
			System.err.println("Compiler " + compiler + " not supported.");
			System.exit(-1);
		}

		// VariableMapper: must be created AFTER the ConstraintMapper
		this.vm = new OAS2MiniZincVariableMapper(apiSpecificationPath, operationPath, operationType, cm.mr);

	}

	public Boolean isOptionalParameter(String paramName) {
		Map.Entry<String, Boolean> paramFeatures = vm.mr.operationParameters.get(paramName);
		if (paramFeatures != null) {
			return !paramFeatures.getValue();
		} else {
			return null;
		}
	}

	public void setParamToValue(String paramName, String paramValue) {
		cm.setParamToValue(paramName, paramValue);
	}

	public void setParamToValue(String changedParamName, String origParamName, String paramValue) {
		cm.setParamToValue(changedParamName, origParamName, paramValue);
	}

	public void finishConstraintsFile() {
		cm.finishConstraintsFile();
	}

	public void appendConstraintsRedundantSolutions() {
		vm.appendConstraintsRedundantSolutions();
	}

	public Set<String> getOperationParameters() {
		return vm.mr.operationParameters.keySet();
	}

	public Map<String,String> setUpRequest(Map<String,String> mznSolution) {
		return vm.setUpRequest(mznSolution);
	}

	public void resetMapperResources() {
		vm.mr.resetStringIntMapping();
	}

//	public Set<String> getRequiredParameters() {
//		return vm.mr.operationParameters.entrySet().stream().filter(param -> param.getValue().getValue()).map(Map.Entry::getKey).collect(Collectors.toSet());
//	}
}
