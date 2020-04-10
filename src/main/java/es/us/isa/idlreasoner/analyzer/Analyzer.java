package es.us.isa.idlreasoner.analyzer;

import es.us.isa.idlreasoner.compiler.Resolutor;
import es.us.isa.idlreasoner.mapper.AbstractMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static es.us.isa.idlreasoner.compiler.ResolutorCreator.createResolutor;
import static es.us.isa.idlreasoner.mapper.MapperCreator.createMapper;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.parseSpecParamName;
import static es.us.isa.idlreasoner.util.Utils.terminate;


public class Analyzer {

	private Resolutor resolutor;
	private AbstractMapper mapper;
	private boolean needReloadConstraintsFile; // When false, random requests are generated faster

	public Analyzer(String specificationType, String idlPath, String apiSpecificationPath, String operationPath, String operationType) {
		initFilesAndConf();
		needReloadConstraintsFile = true;
		resolutor = createResolutor();
		mapper = createMapper(specificationType, idlPath, apiSpecificationPath, operationPath, operationType);
	}

	public Analyzer(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
		this(specificationType, null, apiSpecificationPath, operationPath, operationType);
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
	
	public Map<String,String> getPseudoRandomValidRequest() {
		Map<String, String> res = new HashMap<>();
		List<Map<String,String>> allRequests = getAllUnSetUpRequests();
		
		if(allRequests.size()!=0) {
			res = allRequests.get(ThreadLocalRandom.current().nextInt(0, allRequests.size()));
		}

		return mapper.setUpRequest(res);
	}

	public Map<String,String> getRandomValidRequest() {
		setupRandomRequestOperation(true);
		return mapper.setUpRequest(resolutor.solve());
	}

	public Map<String,String> getRandomInvalidRequest() {
		setupRandomRequestOperation(false);
		if (!mapper.hasDeps())
			return null;
		return mapper.setUpRequest(resolutor.solve());
	}

	public Boolean isDeadParameter(String parameter) {
		setupAnalysisOperation();

		mapper.setParamToValue(parseSpecParamName(parameter)+"Set", "1");
		mapper.finishConstraintsFile();

		return resolutor.solve() == null;
	}

	public Boolean isFalseOptional(String parameter) {
		setupAnalysisOperation();

		if (mapper.isOptionalParameter(parameter)) {
			mapper.setParamToValue(parseSpecParamName(parameter)+"Set", "0");
			mapper.finishConstraintsFile();
			return resolutor.solve() == null;
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
		return resolutor.solve() != null;
	}

	public Boolean isValidRequest(Map<String, String> parametersSet) {
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
		return resolutor.solve() != null;
	}

	public Boolean isValidPartialRequest(Map<String, String> parametersSet) {
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
		return resolutor.solve() != null;
	}

	public Integer numberOfRequest() {
		return this.getAllUnSetUpRequests().size();
	}

	private void setupAnalysisOperation() {
		if (!needReloadConstraintsFile)
			needReloadConstraintsFile = true;
		if (resolutor.isRandomSearch())
			resolutor.setRandomSearch(false);
		mapper.resetCurrentProblem();
		mapper.resetStringIntMapping();
	}

	private void setupRandomRequestOperation(boolean valid) {
		if (needReloadConstraintsFile) {
			setupAnalysisOperation();
			if (!valid)
				mapper.inverseConstraints();
			mapper.finishConstraintsFileWithSearch();
			resolutor.setRandomSearch(true);
			needReloadConstraintsFile = false;
		}
	}

	public void updateData(Map<String, List<String>> data) {
		recreateFile(DATA_FILE);
		try {
			mapper.initializeStringIntMapping();
			mapper.updateDataFile(data);
		} catch (IOException e) {
			terminate("There was an error while creating the data file. Try again.", e);
		}
		mapper.fixStringToIntCounter();
	}














}
