package es.us.isa.idlreasoner.analyzer;


import es.us.isa.idlreasoner.compiler.ResolutorCreator;
import es.us.isa.idlreasoner.mapper.MiniZincMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static es.us.isa.idlreasoner.util.FileManager.copyFile;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.parseParamName;


public class Analyzer {

	private ResolutorCreator resolutor;
	private MiniZincMapper mapper;
//	private Map<String, String> restrictions = new HashMap<String, String>();

	public Analyzer(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		initFilesAndConf();
		resolutor = new ResolutorCreator();
		mapper = new MiniZincMapper(specificationType, idl, apiSpecificationPath, operationPath, operationType);
	}
	
	public List<Map<String,String>> getAllRequest() {

		setupAnalysisOperation();
		mapper.finishConstraintsFile();
		return resolutor.solveGetAllSolutions();
	}
	
	public Map<String,String> randomRequest() {
		Map<String, String> res = new HashMap<>();
		List<Map<String,String>> allRequest = this.getAllRequest();
		
		if(allRequest.size()!=0) {
			res = allRequest.get(ThreadLocalRandom.current().nextInt(0, allRequest.size()));
		}
		return res;
	}

	public Boolean isDeadParameter(String parameter) {
		setupAnalysisOperation();

		mapper.setParamToValue(parseParamName(parameter)+"Set", "1");
		mapper.finishConstraintsFile();

		return !isValidSolution(this.resolutor.solve());
	}

	public Boolean isFalseOptional(String parameter) {
		setupAnalysisOperation();

		if (mapper.isOptionalParameter(parameter)) {
			mapper.setParamToValue(parseParamName(parameter)+"Set", "0");
			mapper.finishConstraintsFile();
			return !isValidSolution(this.resolutor.solve());
		} else {
			return false;
		}
	}

	public Boolean isValidIDL() {
		Set<String> parameters = mapper.getOperationParameters();
		boolean res = true;
		for(String parameter : parameters) {
			res = !this.isDeadParameter(parameter) && !this.isFalseOptional(parameter);
			if(!res)
				break;
		}
		if (res)
			res = isSolvableIDL();
		return res;
	}

	private Boolean isSolvableIDL() {
		setupAnalysisOperation();
		mapper.finishConstraintsFile();
		return isValidSolution(this.resolutor.solve());
	}
	
//	public void setParameter(String parameter, String value) {
//		restrictions.put(parameter, value);
//	}

//	public void setListParameterToVoid() {
//		restrictions.clear();
//	}

	public Boolean validRequest(Map<String, String> parametersSet) {
		setupAnalysisOperation();
		Set<String> parametersSetNames = parametersSet.keySet();
		Set<String> operationParameters = mapper.getOperationParameters();
		for(String operationParameter : operationParameters) {
			if (parametersSetNames.contains(operationParameter)) {
				mapper.setParamToValue(parseParamName(operationParameter), operationParameter, parametersSet.get(operationParameter));
				mapper.setParamToValue(parseParamName(operationParameter)+"Set", "1");
			} else {
				mapper.setParamToValue(parseParamName(operationParameter)+"Set", "0");

			}
		}
		mapper.finishConstraintsFile();
		return isValidSolution(this.resolutor.solve());
	}

	public Boolean validPartialRequest(Map<String, String> parametersSet) {
		setupAnalysisOperation();
		Set<String> parametersSetNames = parametersSet.keySet();
		Set<String> operationParameters = mapper.getOperationParameters();
		for(String operationParameter : operationParameters) {
			if (parametersSetNames.contains(operationParameter)) {
				mapper.setParamToValue(parseParamName(operationParameter), operationParameter, parametersSet.get(operationParameter));
				mapper.setParamToValue(parseParamName(operationParameter)+"Set", "1");
			}
		}
		mapper.finishConstraintsFile();
		return isValidSolution(this.resolutor.solve());
	}

	public Integer numberOfRequest() {
		return this.getAllRequest().size();
	}

	/**
	 * Evaluates whether a map containing the parameters to be set in a request
	 * (i.e. a solution returned by the solver) is right or not. If the operation
	 * contains no parameters nor dependencies, an empty map should be considered
	 * right. Since an empty map is considered as "no solution", instead, a map
	 * containing just one entry (SOLUTION_SEP -> SOLUTION_SEP) is returned.
	 * @param solution Map containing the parameters settings
	 * @return True if the solution is valid, false otherwise
	 */
	private Boolean isValidSolution(Map<String, String> solution) {
		if (solution.size()==1 && solution.get(SOLUTION_SEP).equals(SOLUTION_SEP))
			return true;
		else
			return solution.size()!=0;
	}

	private void setupAnalysisOperation() {
		recreateFile(FULL_CONSTRAINTS_FILE);
		copyFile(BASE_CONSTRAINTS_FILE, FULL_CONSTRAINTS_FILE);
	}

//	public Map<String,String> randomSetUpRequest() {
//		Map<String, String> setUpRequest = new HashMap<>();
//		List<Map<String,String>> allRequest = this.getAllRequest();
//
//		if(allRequest.size()!=0) {
//			setUpRequest = setUpRequest(allRequest.get(ThreadLocalRandom.current().nextInt(0, allRequest.size())));
//		}
//		return setUpRequest;
//	}

//	public List<Map<String,String>> getAllSetUpRequest() {
//		List<Map<String,String>> allSetUpRequest = new ArrayList<>();
//
//		setupAnalysisOperation();
//		this.constraintMapper.finishConstraintsFile();
//		resolutor.solveGetAllSolutions().forEach(solution -> allSetUpRequest.add(setUpRequest(solution)));
//
//		try {
//			recreateFile("src/test/resources/foursquare_test_cases.json");
//			ObjectMapper mapper = new ObjectMapper();
//			String json = null;
//			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(allSetUpRequest);
//			appendContentToFile("src/test/resources/foursquare_test_cases.json", json);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//
//		return allSetUpRequest;
//	}

//	/**
//	 * This method takes as input a solution provided by MiniZinc, i.e. a map of
//	 * variables with their values (including the pSets) and returns a request ready
//	 * to be instantiated, i.e. a map without the pSets, containing only the ps
//	 * whose pSets are equal to 1, replacing parameter names to original names
//	 * (e.g. type_R -> type) and replacing int values with strings.
//	 *
//	 * @param cspSolution Original solution from MiniZinc
//	 * @return Request ready to be instantiated (e.g. by RESTest)
//	 */
//	private Map<String,String> setUpRequest(Map<String,String> cspSolution) {
//		Map<String,String> request = new HashMap<>();
//		Iterator<Map.Entry<String, String>> cspVariables = cspSolution.entrySet().iterator();
//		Map.Entry<String, String> currentCspVariable;
//		String key;
//		String value;
//
//		while (cspVariables.hasNext()) {
//			currentCspVariable = cspVariables.next();
//			key = currentCspVariable.getKey();
//			value = currentCspVariable.getValue();
//			if (cspSolution.get(key + "Set") != null) {
//				if (cspSolution.get(key + "Set").equals("1")) {
//					String nameMapping = variableMapper.getParameterNamesMapping().get(key);
//					if (nameMapping != null) {
//						key = nameMapping;
//					}
//					String finalKey = key;
//					Variable parameter = variableMapper.getVariables().stream().filter(var -> var.getName().equals(finalKey)).findFirst().orElse(null);
//					if (parameter != null) {
//						if (parameter.getType().equals("string")) {
//							String finalValue = value;
//							Map.Entry<String,Integer> intEntry = variableMapper.getStringIntMapping().entrySet().stream().filter(stringIntMapping -> stringIntMapping.getValue().equals(new Integer(finalValue))).findFirst().orElse(null);
//							if (intEntry != null)	 {
//								value = intEntry.getKey();
//							} else {
//								value = "default string";
//							}
//						}
//					}
//					request.put(key, value);
//				}
//			}
//		}
//
//		return request;
//	}
	
	
	
}
