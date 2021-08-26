package es.us.isa.idlreasoner.analyzer;

import es.us.isa.idlreasoner.compiler.Resolutor;
import es.us.isa.idlreasoner.mapper.AbstractMapper;
import es.us.isa.idlreasoner.util.CommonResources;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static es.us.isa.idlreasoner.compiler.ResolutorCreator.createResolutor;
import static es.us.isa.idlreasoner.mapper.MapperCreator.createMapper;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.*;


public class Analyzer {

	private AbstractAnalyzer analyzer;

	public Analyzer(String specificationType, String idlPath, String apiSpecificationPath, String operationPath, String operationType) {
		CommonResources cr = new CommonResources();
		initFilesAndConf(cr);
		if(ANALYZER.toLowerCase().equals("caleta")) {
			analyzer = new CALETAnalyzer(specificationType, idlPath, apiSpecificationPath, operationPath, operationType);
		}else if(ANALYZER.toLowerCase().equals("minizinc")){
			analyzer = new MinizincAnalyzer(specificationType, idlPath, apiSpecificationPath, operationPath, operationType, cr);
		}else {
			throw new UnsupportedOperationException("Unknown Analyzer!");
		}
	}

	public Analyzer(String specificationType, String apiSpecificationPath, String operationPath, String operationType) {
		this(specificationType, null, apiSpecificationPath, operationPath, operationType);
	}

	public List<Map<String,String>> getAllRequests() {
		return analyzer.getAllRequests();
	}
	
	public Map<String,String> getPseudoRandomValidRequest() {
		return analyzer.getPseudoRandomValidRequest();
	}

	public Map<String,String> getRandomValidRequest() {
		return analyzer.getRandomValidRequest();
	}

	public Map<String,String> getRandomInvalidRequest() {
		return analyzer.getRandomInvalidRequest();
	}

	public Boolean isDeadParameter(String parameter) {
		return analyzer.isDeadParameter(parameter);
	}

	public Boolean isFalseOptional(String parameter) {
		return analyzer.isFalseOptional(parameter);
	}

	public Boolean isValidIDL() {
		return analyzer.isValidIDL();
	}

	public Boolean isValidRequest(Map<String, String> parametersSet) {
		return analyzer.isValidRequest(parametersSet);
	}

	public Boolean isValidRequest(Map<String, String> parametersSet, boolean useDefaultData) {
		return analyzer.isValidRequest(parametersSet, useDefaultData);
	}

	public Boolean isValidPartialRequest(Map<String, String> parametersSet) {
		return analyzer.isValidPartialRequest(parametersSet);
	}

	public Integer numberOfRequest() {
		return analyzer.numberOfRequest();
	}
	
	public List<String> whyIsDeadParameter(String parameter) {
		return analyzer.whyIsDeadParameter(parameter);
	}
	
	public List<String> whyIsFalseOptional(String paramter) {
		return analyzer.whyIsFalseOptional(paramter);
	}
	
	public List<String> whyIsNotValidIDL() {
		return analyzer.whyIsNotValidIDL();
	}
	
	public List<String> whyIsNotValidRequest(Map<String, String> parametersSet, boolean useDefaultData) {
		return analyzer.whyIsNotValidRequest(parametersSet, useDefaultData);
	}
	
	public List<String> whyIsNotValidPartialRequest(Map<String, String> parametersSet) {
		return analyzer.whyIsNotValidPartialRequest(parametersSet);
	}
	

	public void updateData(Map<String, List<String>> data) {
		analyzer.updateData(data);
	}












}
