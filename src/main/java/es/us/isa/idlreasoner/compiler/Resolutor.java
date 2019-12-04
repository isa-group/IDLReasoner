package es.us.isa.idlreasoner.compiler;

import java.util.List;
import java.util.Map;

public class Resolutor implements ResolutorInterface{

	
	public Resolutor() {
	}
	
	@Override
	public Map<String,String> solve() {
		System.out.println("There is no compiler defined");
		return null;

	}

	@Override
	public List<Map<String,String>> solveGetAllSolutins(String maxResults) {
		this.solve();
		return null;
	}


}
