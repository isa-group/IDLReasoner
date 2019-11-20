package compiler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Resolutor implements ResolutorInterface{

	
	public Resolutor(String routeFile) {
	}
	
	@Override
	public Map<String,String> solve(String file) {
		System.out.println("There is no compiler defined");
		return null;

	}

	@Override
	public List<Map<String,String>> solveGetAllSolutins(String file, String maxResults) {
		this.solve(file);
		return null;
	}


}
