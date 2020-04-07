package es.us.isa.idlreasoner.compiler;

import java.util.List;
import java.util.Map;

public abstract class Resolutor {

	boolean randomSearch;
	
	public abstract Map<String,String> solve();
	
	public abstract List<Map<String,String>> solveGetAllSolutions();

	public boolean isRandomSearch() {
		return randomSearch;
	}

	public void setRandomSearch(boolean randomSearch) {
		this.randomSearch = randomSearch;
	}
}
