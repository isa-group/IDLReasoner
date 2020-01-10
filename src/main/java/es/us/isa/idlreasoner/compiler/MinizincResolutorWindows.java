package es.us.isa.idlreasoner.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class MinizincResolutorWindows extends MinizincResolutor{
	
	private String solver;
	
	public MinizincResolutorWindows(String solver) {
		super(solver);
		this.solver = solver;
	}

	private String Wconsole = "cmd.exe";
	
	public List<Map<String,String>> solveGetAllSolutions(String maxResults) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		String command;
		if(!maxResults.trim().equals("")){
			command = "\"minizinc/mzn2fzn.exe\" " + FULL_CONSTRAINTS_FILE + " && \"minizinc/fzn-gecode.exe\" -n " + maxResults + " " + FULL_CONSTRAINTS_FILE_FZN + " | \"minizinc/solns2out.exe\" " + FULL_CONSTRAINTS_FILE_OZN;
		}else {
			command = "\"minizinc/mzn2fzn.exe\" " + FULL_CONSTRAINTS_FILE + " && \"minizinc/fzn-gecode.exe\" -a " + FULL_CONSTRAINTS_FILE_FZN + " | \"minizinc/solns2out.exe\" " + FULL_CONSTRAINTS_FILE_OZN;
		}
		String results = this.callSolver(command);
		
		List<String> resultsSplitted = Arrays.asList(results.split(SOLUTION_SEP));
		
		for(String r: resultsSplitted) {
			res.add(this.mapSolutions(r));
		}
		
		res.remove(res.get(res.size()-1));
		return res;
	}
	
	public Map<String,String> solve() {
		String command = "\"minizinc/mzn2fzn.exe\" " + FULL_CONSTRAINTS_FILE + " && \"minizinc/fzn-gecode.exe\" " + FULL_CONSTRAINTS_FILE_FZN + " | \"minizinc/solns2out.exe\" " + FULL_CONSTRAINTS_FILE_OZN;
		String solutions =  this.callSolver(command);
		return this.mapSolutions(solutions);
	}
	
	private Map<String,String> mapSolutions(String solutions){
		Map<String, String> res = new HashMap<String, String>();
		List<String> solutionsSpliitd = Arrays.asList(solutions.split(";"));
		// The following happens when the op has no params nor deps. The solution is empty but valid
		if (solutionsSpliitd.size()==1 && solutionsSpliitd.get(0).contains(SOLUTION_SEP)) {
			res.put(SOLUTION_SEP, SOLUTION_SEP); // A Map containing this identified as a request for an op without params
			return res;
		}
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

	

}
