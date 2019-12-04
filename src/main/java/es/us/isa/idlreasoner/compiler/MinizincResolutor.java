package es.us.isa.idlreasoner.compiler;

import java.util.List;
import java.util.Map;

public class MinizincResolutor extends Resolutor {
	

	public MinizincResolutor(String solver) {
		super();
	}

	public Map<String,String> solve() {
		
		System.out.println("This Operative System is not soported");
		return null;
	}

	public List<Map<String,String>>  solveGetAllSolutins(String maxResults) {
		this.solve();
		return null;
	}

}
