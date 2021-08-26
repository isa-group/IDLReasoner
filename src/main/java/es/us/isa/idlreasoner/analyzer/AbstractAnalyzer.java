package es.us.isa.idlreasoner.analyzer;

import java.util.List;
import java.util.Map;

public abstract interface AbstractAnalyzer {
		
	public abstract List<Map<String, String>> getAllRequests();
	public abstract Map<String,String> getPseudoRandomValidRequest();
	public abstract Map<String,String> getRandomInvalidRequest();
	public abstract Map<String,String> getRandomValidRequest();
	public abstract Boolean isDeadParameter(String parameter);
	public abstract Boolean isFalseOptional(String parameter);
	public abstract Boolean isValidIDL();
	public abstract Boolean isValidRequest(Map<String, String> parametersSet);
	public abstract Boolean isValidRequest(Map<String, String> parametersSet, boolean useDefaultData);
	public abstract Boolean isValidPartialRequest(Map<String, String> parametersSet);
	public abstract Integer numberOfRequest();
	public abstract List<String> whyIsDeadParameter(String parameter);
	public abstract List<String> whyIsFalseOptional(String paramter);
	public abstract List<String> whyIsNotValidIDL();
	public abstract List<String> whyIsNotValidRequest(Map<String, String> parametersSet, boolean useDefaultData);	
	public abstract List<String> whyIsNotValidPartialRequest(Map<String, String> parametersSet);
	public abstract void updateData(Map<String, List<String>> data);


}
