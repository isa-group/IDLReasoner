package es.us.isa.idlreasoner.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class WindowsResolutor extends Resolutor {

	public WindowsResolutor() {}

	public List<Map<String,String>> solveGetAllSolutions() {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		String command;
		if(!MAX_RESULTS.trim().equals(""))
			command = "\"minizinc/minizinc.exe\" -n "+ MAX_RESULTS + " --solver " + SOLVER + " " + FULL_CONSTRAINTS_FILE + " " + DATA_FILE;
		else
			command = "\"minizinc/minizinc.exe\" -a --solver " + SOLVER + " " + FULL_CONSTRAINTS_FILE + " " + DATA_FILE;

		String results = this.callSolver(command);
		results = fixIfErrors(results, command);
		
		List<String> resultsSplit = Arrays.asList(results.split(SOLUTION_SEP));
		
		for(String r: resultsSplit) {
			res.add(this.mapSolutions(r));
		}
		
		res.remove(res.get(res.size()-1));
		return res;
	}
	
	public Map<String,String> solve() {
		String command;
		if (randomSearch)
			command = "\"minizinc/minizinc.exe\" -r " + (new Date().getTime())/1000 + " --solver Gecode " + FULL_CONSTRAINTS_FILE + " " + DATA_FILE;
		else
			command = "\"minizinc/minizinc.exe\" --solver " + SOLVER + " " + FULL_CONSTRAINTS_FILE + " " + DATA_FILE;
		String solutions =  this.callSolver(command);
		solutions = fixIfErrors(solutions, command);
		return this.mapSolutions(solutions);
	}
	
	private Map<String,String> mapSolutions(String solutions){
		Map<String, String> res = new HashMap<String, String>();
		List<String> solutionsSplit = Arrays.asList(solutions.split(";"));
		if (solutionsSplit.size()==1) {
//			res.put(SOLUTION_SEP, SOLUTION_SEP); // A Map containing this identified as a request for an op without params
			if (solutionsSplit.get(0).contains(SOLUTION_SEP) || "".equals(solutionsSplit.get(0))) // This happens when the op has no params nor deps. The solution is empty but valid
				return res;
			else
				return null; // No solution
		}
		String[] aux;
		for(String sol: solutionsSplit) {
			if(!solutionsSplit.get(solutionsSplit.size() - 1).equals(sol)) {
				aux = sol.split("=");
				res.put(aux[0].trim(), aux[1].trim());
			}
		}
		return res;
	}
	
	private String callSolver(String command) {
		String res = "";
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("cmd.exe", "/c", command);
	
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

		return res.equals("") ? "NO SOLUTION" : res;
	}

	private String fixIfErrors(String solutions, String command) {
		if (SOLVER.toLowerCase().equals("chuffed") && solutions.contains("=====ERROR=====")) {
			String newCommand = command.replace(SOLVER, "Gecode");
			return this.callSolver(newCommand);
		}

		return solutions;
	}

	

}