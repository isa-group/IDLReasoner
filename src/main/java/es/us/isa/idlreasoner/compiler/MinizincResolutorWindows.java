package es.us.isa.idlreasoner.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public class MinizincResolutorWindows extends MinizincResolutor{
	
	private String fileRoute;
	private String solver; 
	
	public MinizincResolutorWindows(String fileRoute, String solver) {
		super(fileRoute, solver);
		this.fileRoute = fileRoute;
		this.solver = solver;
	}

	private String Wconsole = "cmd.exe";
	
	public List<Map<String,String>> solveGetAllSolutins(String fileName, String maxResults) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		
		String command = "\"minizinc/minizinc.exe\" -n "+ maxResults + " --solver " + solver + " " + readProperty("aux_files_folder") +"/"+readProperty("constraints_file");

		String results = this.callSolver(command);
		
		List<String> resultsSplitted = Arrays.asList(results.split("----------"));
		
		for(String r: resultsSplitted) {
			res.add(this.mapSolutions(r));
		}
		
		res.remove(res.get(res.size()-1));
		
		return res;
	}
	
	public Map<String,String> solve(String fileName) {
		String command = "\"minizinc/minizinc.exe\" --solver " + solver + " " + readProperty("aux_files_folder") +"/"+readProperty("constraints_file");
		String solutions =  this.callSolver(command);
		return this.mapSolutions(solutions);
	}
	
	private Map<String,String> mapSolutions(String solutions){
		Map<String, String> res = new HashMap<String, String>();
		List<String> solutionsSpliitd = Arrays.asList(solutions.split(";"));
		String[] aux;
		for(String sol: solutionsSpliitd) {
			if(solutionsSpliitd.get(solutionsSpliitd.size()-1)!=sol) {
				aux = sol.split("=");
				res.put(aux[0].trim(), aux[1].trim());
			}
		}
		return res;
	}
	
	private String callSolver(String command) {
		String res = "";
		
		ProcessBuilder processBuilder = new ProcessBuilder();

		
		processBuilder.command(Wconsole, "/c", command);
	
		try {

           	Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                res+=line+"\n";
            	}
	        } catch (IOException e) {
	            e.printStackTrace();
	        } 
		

		return res;
	}

	

}
