package es.us.isa.idlreasoner.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static es.us.isa.idlreasoner.util.IDLConfiguration.BASE_CONSTRAINTS_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.FULL_CONSTRAINTS_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.FULL_CONSTRAINTS_FILE_FZN;

public class MinizincResolutorWindows extends MinizincResolutor{
	
	private String solver;
	
	public MinizincResolutorWindows(String solver) {
		super(solver);
		this.solver = solver;
	}

	private String Wconsole = "cmd.exe";
	
	public List<Map<String,String>> solveGetAllSolutins(String maxResults) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		String command;
		this.convertToFzn();
		if(!maxResults.trim().equals("")){
			command = "\"minizinc/fzn-gecode.exe\" -n "+ maxResults+ " "+ FULL_CONSTRAINTS_FILE_FZN;
		}else {
			command = "\"minizinc/fzn-gecode.exe\" -a " + FULL_CONSTRAINTS_FILE_FZN;
		}
		String results = this.callSolver(command);
		List<String> resultsSplitted = Arrays.asList(results.split("----------"));
		
		for(String r: resultsSplitted) {
			res.add(this.mapSolutions(r));
		}
		
		res.remove(res.get(res.size()-1));
		return res;
	}
	
	public Map<String,String> solve() {
		this.convertToFzn();
		String command = "\"minizinc/fzn-gecode.exe\" " + FULL_CONSTRAINTS_FILE_FZN;
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
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(Wconsole, "/c", command);
		String res = "";
		try {
           	Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                res+=line+"\n";
			}

            reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	private void convertToFzn() {
		String command = "\"minizinc/mzn2fzn.exe\" " + FULL_CONSTRAINTS_FILE;
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(Wconsole, "/c", command);
       	try {
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
