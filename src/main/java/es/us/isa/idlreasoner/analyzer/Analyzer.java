package es.us.isa.idlreasoner.analyzer;

import es.us.isa.idlreasoner.compiler.ResolutorCreator;
import es.us.isa.idlreasoner.mapper.MiniZincMapper;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static es.us.isa.idlreasoner.util.FileManager.copyFile;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.parseSpecParamName;


public class Analyzer {

	private ResolutorCreator resolutor;
	private MiniZincMapper mapper;

	public Analyzer(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		initFilesAndConf();
		resolutor = new ResolutorCreator();
		mapper = new MiniZincMapper(specificationType, idl, apiSpecificationPath, operationPath, operationType);
	}

	public Analyzer(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
		initFilesAndConf();
		resolutor = new ResolutorCreator();
		mapper = new MiniZincMapper(specificationType, apiSpecificationPath, operationPath, operationType);
	}
	
	public List<Map<String,String>> getAllRequests() {
		List<Map<String,String>> setUpRequests = new ArrayList<>();
		getAllUnSetUpRequests().forEach(r -> setUpRequests.add(mapper.setUpRequest(r)));
		return setUpRequests;
	}

	private List<Map<String,String>> getAllUnSetUpRequests() {
		setupAnalysisOperation();
		mapper.appendConstraintsRedundantSolutions();
		mapper.finishConstraintsFile();
		return resolutor.solveGetAllSolutions();
	}
	
	public Map<String,String> randomRequest() {
		Map<String, String> res = new HashMap<>();
		List<Map<String,String>> allRequests = getAllUnSetUpRequests();
		
		if(allRequests.size()!=0) {
			res = allRequests.get(ThreadLocalRandom.current().nextInt(0, allRequests.size()));
		}

		return mapper.setUpRequest(res);
	}

	public Boolean isDeadParameter(String parameter) {
		setupAnalysisOperation();

		mapper.setParamToValue(parseSpecParamName(parameter)+"Set", "1");
		mapper.finishConstraintsFile();

		return !isValidSolution(this.resolutor.solve());
	}

	public Boolean isFalseOptional(String parameter) {
		setupAnalysisOperation();

		if (mapper.isOptionalParameter(parameter)) {
			mapper.setParamToValue(parseSpecParamName(parameter)+"Set", "0");
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

	public Boolean validRequest(Map<String, String> parametersSet) {
		setupAnalysisOperation();
		Set<String> parametersSetNames = parametersSet.keySet();
		Set<String> operationParameters = mapper.getOperationParameters();
		for(String operationParameter : operationParameters) {
			if (parametersSetNames.contains(operationParameter)) {
				mapper.setParamToValue(parseSpecParamName(operationParameter), operationParameter, parametersSet.get(operationParameter));
				mapper.setParamToValue(parseSpecParamName(operationParameter)+"Set", "1");
			} else {
				mapper.setParamToValue(parseSpecParamName(operationParameter)+"Set", "0");

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
				mapper.setParamToValue(parseSpecParamName(operationParameter), operationParameter, parametersSet.get(operationParameter));
				mapper.setParamToValue(parseSpecParamName(operationParameter)+"Set", "1");
			}
		}
		mapper.finishConstraintsFile();
		return isValidSolution(this.resolutor.solve());
	}

	public Integer numberOfRequest() {
		return this.getAllUnSetUpRequests().size();
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
		mapper.resetMapperResources();
	}
}
