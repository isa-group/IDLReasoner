package es.us.isa.idlreasoner.compiler;

import java.util.List;
import java.util.Map;

public interface ResolutorInterface {
	
	public Map<String,String> solve();
	
	public List<Map<String,String>> solveGetAllSolutions(String maxResults);

}
