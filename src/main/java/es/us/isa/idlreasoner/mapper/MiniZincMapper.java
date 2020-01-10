package es.us.isa.idlreasoner.mapper;

import java.util.Map;
import java.util.Set;

import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public class MiniZincMapper {

	private MiniZincConstraintMapper cm;
	private AbstractVariableMapper vm;
	
	public MiniZincMapper(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		String compiler = readProperty("compiler");

		// ConstraintMapper: must be created BEFORE the VariableMapper
		if(compiler.toLowerCase().equals("minizinc"))
			this.cm = new MiniZincConstraintMapper(idl, null);
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

	public Set<String> getOperationParameters() {
		return vm.mr.operationParameters.keySet();
	}

	public Map<String,String> setUpRequest(Map<String,String> mznSolution) {
		return vm.setUpRequest(mznSolution);
	}

//	public Set<String> getRequiredParameters() {
//		return vm.mr.operationParameters.entrySet().stream().filter(param -> param.getValue().getValue()).map(Map.Entry::getKey).collect(Collectors.toSet());
//	}
}
