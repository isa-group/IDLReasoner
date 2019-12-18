package es.us.isa.idlreasoner.analyzer;


import es.us.isa.idlreasoner.compiler.ResolutorCreator;
import es.us.isa.idlreasoner.mapper.*;
import es.us.isa.idlreasoner.pojos.Variable;

import static es.us.isa.idlreasoner.util.FileManager.copyFile;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		List<Map<String,String>> allRequest = this.getAllRequest();
		
		if(allRequest.size()!=0) {
			allRequest.get(ThreadLocalRandom.current().nextInt(0, allRequest.size()));
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
		
		File file = new File("./idl_aux_files/config.properties");
		Boolean exists = file.canRead();

		//TODO: Use FileManager class for all file-related operations (create file, append content, etc.)
		if(!exists) {
			file.getParentFile().mkdir();
			FileWriter fw;

			try {
				file.createNewFile();
				
				fw = new FileWriter(file);

			    fw.append("compiler: Minizinc\n");
			    fw.append("solver: Chuffed\n");
			    fw.append("mapper: idl\n"); //TODO: delete this
			    fw.append("specification: oas\n"); //TODO: delete this
			    fw.append("fileRoute: " + readProperty("aux_files_folder") + "/" + readProperty("idl_files_folder") + "\n");
			    fw.append("maxResults: 100\n");
			    
			    fw.flush();
			    fw.close();
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}

		updateConf();
	}
	
	
	
}
