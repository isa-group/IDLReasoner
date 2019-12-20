package es.us.isa.idlreasoner.analyzer;


import es.us.isa.idlreasoner.compiler.ResolutorCreator;
import es.us.isa.idlreasoner.mapper.*;
import es.us.isa.idlreasoner.pojos.Variable;
import es.us.isa.idlreasoner.util.FileManager;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import java.util.stream.Collectors;


public class Analyzer {

	private ResolutorCreator resolutor;
	private MapperCreator mapperCreator;
	private AbstractConstraintMapper constraintMapper;
	private AbstractVariableMapper variableMapper;
	private Map<String, Map<String, Integer>> mappingParameters;
	
	
	private Map<String, String> restrictions = new HashMap<String, String>();

	public Analyzer(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		
		this.initConfigurationFile();
		
		recreateFile(BASE_CONSTRAINTS_FILE);
		
		this.resolutor = new ResolutorCreator();
		this.mapperCreator = new MapperCreator(specificationType, idl, apiSpecificationPath, operationPath, operationType);
		
		this.constraintMapper = this.mapperCreator.getConstraintMapper();
		this.variableMapper = this.mapperCreator.getVariableMapper();


	}
	
	
	public List<Map<String,String>>  getAllRequest() {

		setupAnalysisOperation();
		this.constraintMapper.finishConstraintsFile();
		return resolutor.solveGetAllSolutins();
	}
	
	public Map<String,String> randomRequest() {
		Map<String, String> res = new HashMap<>();
		Map<String, String> res2 = new HashMap<>();
		List<Map<String,String>> allRequest = this.getAllRequest();
		
		if(allRequest.size()!=0) {
			res = allRequest.get(ThreadLocalRandom.current().nextInt(0, allRequest.size()));
			res2 = setUpRequest2(res);
		}
		return res;
	
	}


	public Boolean isDeadParameter(String parameter) {
		setupAnalysisOperation();

		this.constraintMapper.setParamToValue(parameter+"Set", "1");
		this.constraintMapper.finishConstraintsFile();

		return resolutor.solve().size()==0;
	}
	


	public Boolean isFalseOptional(String parameter) {
		setupAnalysisOperation();

		Variable parameterVar = this.variableMapper.getVariables().stream()
				.filter(var -> var.getName().equals(parameter))
				.findFirst().orElse(null);
		if (parameterVar != null && !parameterVar.getRequired()) {
			this.constraintMapper.setParamToValue(parameter+"Set", "0");
			this.constraintMapper.finishConstraintsFile();
			return resolutor.solve().size()==0;
		} else {
			return false;
		}
	}
	
	
	public Boolean isValidIDL() {
		List<String> parameters = this.variableMapper.getVariables().stream().map(Variable::getName).collect(Collectors.toList());
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
		if(this.mappingParameters==null) {
			this.mappingParameters =this.variableMapper.getMappingParameters();
		}
		restrictions.put(parameter, Integer.toString(translateParameter(parameter, value)));
	}
	
	
	public void setListParameterToVoid() {
		this.restrictions = new HashMap<String, String>();
	}
	

	public Boolean validRequest() {
		setupAnalysisOperation();
		Set<String> parameters = this.restrictions.keySet();
		List<String> allParameters = this.variableMapper.getVariables().stream().map(Variable::getName).collect(Collectors.toList());
		for(String p : allParameters) {
			if(parameters.contains(p)) {
				this.constraintMapper.setParamToValue(p, this.restrictions.get(p));
				this.constraintMapper.setParamToValue(p+"Set", "1");
			}else {
				this.constraintMapper.setParamToValue(p+"Set", "0");

			}
		}
		this.constraintMapper.finishConstraintsFile();
		return this.resolutor.solve().size()!=0;
	}
	

	public Boolean validPartialRequest() {
		Set<String> parameters = this.restrictions.keySet();
		List<String> allParameters = this.variableMapper.getVariables().stream().map(Variable::getName).collect(Collectors.toList());
		setupAnalysisOperation();
		for(String p : allParameters) {
			if(parameters.contains(p)) {
				this.constraintMapper.setParamToValue(p, this.restrictions.get(p));
				this.constraintMapper.setParamToValue(p+"Set", "1");
			}
		}
		this.constraintMapper.finishConstraintsFile();
		return this.resolutor.solve().size()!=0;
	}
	

	public Integer numberOfRequest() {
		return this.getAllRequest().size();
	}
	
	private Integer translateParameter(String parameter, String value) {
		Integer res = 1;
		Set<String> parameters = mappingParameters.keySet();
		if(parameters.contains(parameter)) {
			Map<String, Integer> mapping = mappingParameters.get(parameter);
			res = mapping.get(value);
		}
		
		return res;
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

	/**
	 * This method takes as input a solution provided by MiniZinc, i.e. a map of
	 * variables with their values (including the pSets) and returns a request ready
	 * to be instantiated, i.e. a map without the pSets, containing only the ps
	 * whose pSets are equal to 1, replacing parameter names to original names
	 * (e.g. type_R -> type) and replacing int values with strings.
	 *
	 * @param cspSolution Original solution from MiniZinc
	 * @return Request ready to be instantiated (e.g. by RESTest)
	 */
	private Map<String,String> setUpRequest(Map<String,String> cspSolution) {

		Iterator<Map.Entry<String, String>> cspVariables = cspSolution.entrySet().iterator();
		Map.Entry<String, String> currentCspVariable;
		String changedName;
		String originalName;
		Variable parameter;
		String parameterType;
		while (cspVariables.hasNext()) {
			currentCspVariable = cspVariables.next();
			changedName = currentCspVariable.getKey();
			if (cspSolution.get(changedName + "Set") != null) {
				if (cspSolution.get(changedName + "Set").equals("1")) {
					originalName = variableMapper.getParameterNamesMapping().get(changedName);
					if (originalName != null) {
						cspSolution.put(originalName, currentCspVariable.getValue());
					} else {
						originalName = changedName;
					}
					String finalOriginalName = originalName;
					parameter = variableMapper.getVariables().stream().filter(var -> var.getName().equals(finalOriginalName)).findFirst().orElse(null);
					if (parameter != null) {
						parameterType = parameter.getType();
						if (parameterType.equals("string")) {
							cspSolution.put(originalName, "random string");
//							if (variableMapper.getStringIntMapping().containsValue(new Integer(currentCspVariable.getValue()))) {
//
//							}
//							if (variableMapper.getStringIntMapping().get(currentCspVariable.getValue()) != null) {
//								cspSolution.put(originalName, variableMapper.getStringIntMapping().get(currentCspVariable.getValue()))
//							}
						}
					}
					if (!originalName.equals(changedName)) {
						cspSolution.remove(changedName);
					}
					cspSolution.remove(changedName + "Set");
				}
//				else {
//					cspSolution.remove(currentCspVariable.getKey());
//					cspSolution.remove(currentCspVariable.getKey() + "Set");
//				}
			}
//			if (currentCspVariable.getKey().substring(currentCspVariable.getKey().length()-3).equals("Set")) {
//
//			}
		}


		return cspSolution;
	}


	/**
	 * This method takes as input a solution provided by MiniZinc, i.e. a map of
	 * variables with their values (including the pSets) and returns a request ready
	 * to be instantiated, i.e. a map without the pSets, containing only the ps
	 * whose pSets are equal to 1, replacing parameter names to original names
	 * (e.g. type_R -> type) and replacing int values with strings.
	 *
	 * @param cspSolution Original solution from MiniZinc
	 * @return Request ready to be instantiated (e.g. by RESTest)
	 */
	private Map<String,String> setUpRequest2(Map<String,String> cspSolution) {
		Map<String,String> request = new HashMap<>();
		Iterator<Map.Entry<String, String>> cspVariables = cspSolution.entrySet().iterator();
		Map.Entry<String, String> currentCspVariable;
		String key;
		String value;

		while (cspVariables.hasNext()) {
			currentCspVariable = cspVariables.next();
			key = currentCspVariable.getKey();
			value = currentCspVariable.getValue();
			if (cspSolution.get(key + "Set") != null) {
				if (cspSolution.get(key + "Set").equals("1")) {
					if (variableMapper.getParameterNamesMapping().get(key) != null) {
						key = variableMapper.getParameterNamesMapping().get(key);
					}
					String finalKey = key;
					Variable parameter = variableMapper.getVariables().stream().filter(var -> var.getName().equals(finalKey)).findFirst().orElse(null);
					if (parameter != null) {
						if (parameter.getType().equals("string")) {
							String finalValue = value;
							Map.Entry<String,Integer> intEntry = variableMapper.getStringIntMapping().entrySet().stream().filter(stringIntMapping -> stringIntMapping.getValue().equals(new Integer(finalValue))).findFirst().orElse(null);
							if (intEntry != null)	 {
								value = intEntry.getKey();
							} else {
								value = "default string";
							}
						}
					}
					request.put(key, value);
				}
			}
		}

		return request;
	}
	
	
	
}
