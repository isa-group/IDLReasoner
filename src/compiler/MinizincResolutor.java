package compiler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MinizincResolutor extends Resolutor{
	

	public MinizincResolutor(String fileRoute, String solver) {
		super(fileRoute);
	}

	public Map<String,String> solve(String file) {
		
		System.out.println("This Operative System is not soported");
		return null;
	}

	public List<Map<String,String>>  solveGetAllSolutins(String file, String maxResults) {
		this.solve(file);
		return null;
	}

}
