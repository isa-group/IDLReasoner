package es.us.isa.idlreasoner.analyzer;

import static es.us.isa.idlreasoner.util.IDLConfiguration.initFilesAndConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import es.us.isa.idlreasoner.compiler.CALETAResolutor;
import es.us.isa.idlreasoner.mapper.CALETAMapper;
import es.us.isa.idlreasoner.util.CommonResources;

public class CALETAnalyzer implements AbstractAnalyzer {

	private CALETAResolutor resolutor;
	private CALETAMapper mapper;
	
	private Set<String> parameters;
	
	public CALETAnalyzer(String specificationType, String idlPath, String apiSpecificationPath, String operationPath, String operationType) {
		CommonResources cr = new CommonResources();
		initFilesAndConf(cr);
		
		mapper = new CALETAMapper(cr, specificationType,  idlPath, apiSpecificationPath, operationPath, operationType);
		parameters = mapper.getOperationParameters();
		
		resolutor = new CALETAResolutor(apiSpecificationPath, operationPath, operationType, mapper.getMapParameter().values(), mapper.getDependencies());
		setUpListener();
	}
	
	public Boolean isDeadParameter(String parameter) {
		resetDependencies();
		setParamPresence(parameter, true);
		
		return !isSatisfiable();
	}
	
	public Boolean isFalseOptional(String parameter) {
		if(mapper.isRequired(parameter)) {
			return false;
		}
		resetDependencies();
		setParamPresence(parameter, false);
		
		return !isSatisfiable();
	}
	
	public Boolean isValidIDL() {
		Boolean res = true;
		Set<String> parameters = mapper.getOperationParameters();
		for (String parameter : parameters) {
			res = !this.isDeadParameter(parameter) && !this.isFalseOptional(parameter);
			if(!res)
				break;
		}
		if(res)
			res = isSatisfiable();
		return res;
	}
	
	public Boolean isValidRequest(Map<String, String> parametersSet) {
		return isValidRequest(parametersSet, false);
	}
	

	@Override
	public Boolean isValidRequest(Map<String, String> parametersSet, boolean useDefaultData) {
		resetDependencies();
		try {			
			for (String parameter : parameters) {
				if(parametersSet.containsKey(parameter)) {
					setParamPresence(parameter, true);
					setParamValue(parameter, parametersSet.get(parameter));	
				}else if(parametersSet.containsKey(parameter+"Set")) {
					setParamPresence(parameter, true);
				}else {
					setParamPresence(parameter, false);
				}
			}
		}catch(Exception e){
			return false;
			}
		return isSatisfiable();
	}
	
	
	@Override
	public Boolean isValidPartialRequest(Map<String, String> parametersSet) {
		resetDependencies();
		try {
			for (String parameter : parameters) {
				if(parametersSet.keySet().contains(parameter)) {
					setParamPresence(parameter, true);
					setParamValue(parameter, parametersSet.get(parameter));	
				}
			}
		}catch(Exception e){
				return false;
			}
		return isSatisfiable();
	}
	
	public Map<String,String> getPseudoRandomValidRequest() {
		resetDependencies();
		finishModel();
		isSatisfiable();
		return resolutor.getASolution();
	}

	@Override
	public List<Map<String, String>> getAllRequests() {
		List<Map<String, String>> res = new ArrayList<>();
		if(isSatisfiable()) {
			res = resolutor.getAllSolutions();
		}
		return res;
	}


	@Override
	public Integer numberOfRequest() {
		Integer res = 0;
		if(isSatisfiable()) {
			res = getAllRequests().size();
		}
		return res;
	}
	

	@Override
	public Map<String, String> getRandomInvalidRequest() {
		resetDependencies();
		if(!isSatisfiable()) {
			return null;
		}else {
			return resolutor.getRandomNotValidSolution();
		}
	}
	
	
	@Override
	public Map<String, String> getRandomValidRequest() {
		resetDependencies();
		Map<String, String> res = null;
		if(isSatisfiable()) {
			res = resolutor.getRandomSolution();
		}
		return res;
	}

	@Override
	public List<String> whyIsDeadParameter(String parameter) {
		System.out.println("Not implemented");
		return null;
	}

	@Override
	public List<String> whyIsFalseOptional(String paramter) {
		System.out.println("Not implemented");
		return null;
	}

	@Override
	public List<String> whyIsNotValidIDL() {
		System.out.println("Not implemented");
		return null;
	}

	@Override
	public List<String> whyIsNotValidRequest(Map<String, String> parametersSet, boolean useDefaultData) {
		System.out.println("Not implemented");
		return null;
	}

	@Override
	public List<String> whyIsNotValidPartialRequest(Map<String, String> parametersSet) {
		System.out.println("Not implemented");
		return null;
	}

	@Override
	public void updateData(Map<String, List<String>> data) {
		System.out.println("Not implemented");
		
	}
	
	private Boolean isSatisfiable() {
		if(solve()!="UNSATISFIABLE") {
			return true;
		}else {
			return false;
		}
	}
	
	private void setParamValue(String parameter, String value) throws Exception {
		resolutor.addDependency(mapper.putParameterValue(parameter, value));
	}
	
	private void setParamPresence(String parameter, Boolean presence) {
		resolutor.addDependency(mapper.putPresenceParameter(parameter, presence));
	}

	private void setUpListener() {
		resolutor.addListener(); 
	}
	
	private void finishModel() {
		resolutor.finishModel();
	}
	
	private void resetDependencies() {
		resolutor.resetDependencies();
	}
	
	private String solve() {
		finishModel();
		return resolutor.solve();
	}



}
