package es.us.isa.idlreasoner.compiler;

import es.us.isa.idlreasoner.util.CommonResources;
import es.us.isa.idlreasoner.util.StreamGobbler;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class Resolutor {

	protected CommonResources cr;
	protected boolean randomSearch;

	public Resolutor(CommonResources cr) {
		this.cr = cr;
	}

	public boolean isRandomSearch() {
		return randomSearch;
	}

	public void setRandomSearch(boolean randomSearch) {
		this.randomSearch = randomSearch;
	}

	public String getSeed() {
		return ThreadLocalRandom.current().nextInt(1, 2146) + Long.toString((new Date().getTime())/1000).substring(4);
	}

	public List<Map<String,String>> solveGetAllSolutions() {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		String results = null;
		while (results == null)
			results =  this.callSolver(getSolveAllCommand(), false);
//		results = fixIfErrors(results, command);
		List<String> resultsSplit = Arrays.asList(results.split(SOLUTION_SEP));

		if (resultsSplit.size() == 2 && resultsSplit.get(0).equals(""))
			res.add(new HashMap<>());
		else if (resultsSplit.size() > 1) {
			for (String r : resultsSplit)
				res.add(this.mapSolutions(r));
			res.remove(res.get(res.size() - 1));
		}
		return res;
	}

	public Map<String,String> solve() {
		String solutions = null;
		while (solutions == null)
			solutions =  this.callSolver(getSolveCommand(), randomSearch);
//		solutions = fixIfErrors(solutions, command);
		return this.mapSolutions(solutions);
	}

	private Map<String,String> mapSolutions(String solutions){
		Map<String, String> res = new HashMap<String, String>();
		List<String> solutionsSplit = Arrays.asList(solutions.split(";"));
		if (solutionsSplit.size()==1) {
			if (solutionsSplit.get(0).contains(SOLUTION_SEP)) // This happens when the op has no params nor deps. The solution is empty but valid
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

	private String callSolver(String command, boolean async) {
		String res = "";
		ProcessBuilder processBuilder = new ProcessBuilder();
		String[] commandProcessArgs = getCommandProcessArgs();
		processBuilder.command(commandProcessArgs[0], commandProcessArgs[1], command);

		try {
			Process process = processBuilder.start();
			BufferedReader reader;
			if (async) {
				StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream());
				Executors.newSingleThreadExecutor().submit(streamGobbler);
				if (process.waitFor(TIMEOUT, TimeUnit.MILLISECONDS))
					reader = streamGobbler.getReader();
				else
					return null;
			} else
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null)
				res+=line+"\n";
			reader.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		return res;
	}

	private String getSolveCommand() {
		if (randomSearch)
			return getMinizincExe() + " -r " + getSeed() + " --solver Gecode " + cr.BASE_CONSTRAINTS_FILE + " " + cr.DATA_FILE;
		else
			return getMinizincExe() + " --solver " + SOLVER + " " + cr.BASE_CONSTRAINTS_FILE + " " + cr.DATA_FILE;
	}

	private String getSolveAllCommand() {
		if(!MAX_RESULTS.trim().equals(""))
			return getMinizincExe() + " -n "+ MAX_RESULTS + " --solver " + SOLVER + " " + cr.BASE_CONSTRAINTS_FILE + " " + cr.DATA_FILE;
		else
			return getMinizincExe() + " -a --solver " + SOLVER + " " + cr.BASE_CONSTRAINTS_FILE + " " + cr.DATA_FILE;
	}

	private String getMinizincExe() {
		if (SystemUtils.IS_OS_WINDOWS)
			return "\"minizinc/windows/minizinc.exe\"";
		else if (SystemUtils.IS_OS_MAC)
			return "./minizinc/mac/minizinc";
		else if (SystemUtils.IS_OS_LINUX)
			return "./minizinc/linux/bin/minizinc";
		else
			terminate("Operating system " + System.getProperty("os.name") + " not supported.");

		return null;
	}

	private String[] getCommandProcessArgs() {
		String[] commandProcessArgs = new String[2];
		if (SystemUtils.IS_OS_WINDOWS) {
			commandProcessArgs[0] = "cmd.exe";
			commandProcessArgs[1] = "/c";
		}
		else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
			commandProcessArgs[0] = "/bin/bash";
			commandProcessArgs[1] = "-c";
		}
		else
			terminate("Operating system " + System.getProperty("os.name") + " not supported.");

		return commandProcessArgs;
	}

//	private String fixIfErrors(String solutions, String command) {
//		if (SOLVER.toLowerCase().equals("chuffed") && solutions.contains("=====ERROR=====")) {
//			String newCommand = command.replace(SOLVER, "Gecode");
//			return this.callSolver(newCommand);
//		}
//
//		return solutions;
//	}
}
