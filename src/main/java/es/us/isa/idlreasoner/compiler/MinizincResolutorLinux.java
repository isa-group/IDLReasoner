package es.us.isa.idlreasoner.compiler;

import static es.us.isa.idlreasoner.util.IDLConfiguration.FULL_CONSTRAINTS_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.SOLUTION_SEP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import es.us.isa.idlreasoner.util.FileManager;
import es.us.isa.idlreasoner.util.WebContentAuxiliar;

public class MinizincResolutorLinux extends MinizincResolutor{

	
	private String solver;
	private WebContentAuxiliar webContent = new WebContentAuxiliar();


	
	public MinizincResolutorLinux(String solver) {
		super(solver);
		this.solver = solver;
	}
	
	public List<Map<String,String>> solveGetAllSolutions(String maxResults) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		String command;
		if(!maxResults.trim().equals("")){
			command = getMinizincExe()+ " -n "+ maxResults + " --solver " + solver + " " + FULL_CONSTRAINTS_FILE;
		}else {
			command = getMinizincExe()+" -a --solver " + solver + " " + FULL_CONSTRAINTS_FILE;
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
		String command = getMinizincExe() + " --solver " + solver + " " + FULL_CONSTRAINTS_FILE;
		String solutions =  this.callSolver(command);
		return this.mapSolutions(solutions);
	}
	
	
	
	private String callSolver(String command) {
		String res = "";
		String[] args = new String[] {"/bin/bash", "-c", command};
		ProcessBuilder processBuilder = new ProcessBuilder(args);

		try {

           	Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                res+=line+"\n";
			}
            reader.close();
            process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return res;
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
	
	private String getMinizincExe() {
		String res = "\"minizinc-linux/bin/minizinc\"";
		if(webContent.isFromAWebContent()) {
			res = "/minizinc-linux/bin/minizinc";
			res = webContent.getPath(res);
			res = "\"" + res + "\"";
		}
		return res;
	}
}
