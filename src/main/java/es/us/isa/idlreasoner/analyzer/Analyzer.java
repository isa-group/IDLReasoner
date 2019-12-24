package es.us.isa.idlreasoner.analyzer;


import es.us.isa.idlreasoner.compiler.ResolutorCreator;
import es.us.isa.idlreasoner.mapper.*;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;
import static es.us.isa.idlreasoner.util.Utils.parseParamName;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class Analyzer {

	private ResolutorCreator resolutor;
	private MapperCreator mapper;
//	private AbstractConstraintMapper constraintMapper;
//	private AbstractVariableMapper variableMapper;
	private Map<String, String> restrictions = new HashMap<String, String>();

	public Analyzer(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		
		this.initConfigurationFile();
		
		recreateFile(BASE_CONSTRAINTS_FILE);
		
		this.resolutor = new ResolutorCreator();
		mapper = new MapperCreator(specificationType, idl, apiSpecificationPath, operationPath, operationType);
		
//		this.constraintMapper = mapper.getConstraintMapper();
//		this.variableMapper = mapper.getVariableMapper();


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

		return resolutor.solve().size()==0;
	}

	public Boolean isFalseOptional(String parameter) {
		setupAnalysisOperation();

		if (mapper.isOptionalParameter(parameter)) {
			mapper.setParamToValue(parseParamName(parameter)+"Set", "0");
			mapper.finishConstraintsFile();
			return resolutor.solve().size()==0;
		} else {
			return false;
		}
	}

	public Boolean isValidIDL() {
		Set<String> parameters = mapper.getOperationParameters();
		Boolean res = true;
		for(String parameter : parameters) {
			res = !this.isDeadParameter(parameter) && !this.isFalseOptional(parameter);
			if(!res) { 
				break;
			}
		}
		return res;
	}
	
	public void setParameter(String parameter, String value) {
		restrictions.put(parameter, value);
	}

	public void setListParameterToVoid() {
		restrictions.clear();
	}

	public Boolean validRequest() {
		setupAnalysisOperation();
		Set<String> restrictionParameters = this.restrictions.keySet();
		Set<String> operationParameters = mapper.getOperationParameters();
		for(String operationParameter : operationParameters) {
			if (restrictionParameters.contains(operationParameter)) {
				mapper.setParamToValue(parseParamName(operationParameter), operationParameter, restrictions.get(operationParameter));
				mapper.setParamToValue(parseParamName(operationParameter)+"Set", "1");
			} else {
				mapper.setParamToValue(parseParamName(operationParameter)+"Set", "0");

			}
		}
		mapper.finishConstraintsFile();
		return this.resolutor.solve().size()!=0;
	}

	public Boolean validPartialRequest() {
		setupAnalysisOperation();
		Set<String> restrictionParameters = this.restrictions.keySet();
		Set<String> operationParameters = mapper.getOperationParameters();
		for(String operationParameter : operationParameters) {
			if (restrictionParameters.contains(operationParameter)) {
				mapper.setParamToValue(parseParamName(operationParameter), operationParameter, restrictions.get(operationParameter));
				mapper.setParamToValue(parseParamName(operationParameter)+"Set", "1");
			}
		}
		mapper.finishConstraintsFile();
		return this.resolutor.solve().size()!=0;
	}

	public Integer numberOfRequest() {
		return this.getAllRequest().size();
	}

	private void setupAnalysisOperation() {
		recreateFile(FULL_CONSTRAINTS_FILE);
		copyFile(BASE_CONSTRAINTS_FILE, FULL_CONSTRAINTS_FILE);
	}
	
	private void initConfigurationFile() {
		String filePath = "./idl_aux_files/config.properties";
		createFileIfNotExists(filePath);
		BufferedReader br = openReader(filePath);

			try {
				if(br.readLine()==null) {
					br.close();
					BufferedWriter bw = openWriter(filePath);
					
				    bw.append("compiler: Minizinc\n");
				    bw.append("solver: Chuffed\n");
				    bw.append("fileRoute: " + readProperty("aux_files_folder") + "/" + readProperty("idl_files_folder") + "\n");
				    bw.append("maxResults: 100\n");
				    
				    bw.flush();
				    bw.close();
				} else {
					br.close();
				}
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			updateConf();
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
