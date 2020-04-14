package es.us.isa.idlreasoner.compiler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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

	public String getSeed() {
		return ThreadLocalRandom.current().nextInt(1, 2146) + Long.toString((new Date().getTime())/1000).substring(4);
	}
}
