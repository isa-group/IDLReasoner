package es.us.isa.idlreasoner.compiler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class ResolutorCreator {
	
	private String osName;
	private Resolutor currentCompiler;

	
	public ResolutorCreator() {

		this.osName = System.getProperty("os.name");

		if (COMPILER.equals("Minizinc")) {
			if (this.osName.contains("Windows")) {
				this.currentCompiler = new MinizincResolutorWindows(SOLVER);
			} else if(this.osName.contains("Linux")){
				this.currentCompiler = new MinizincResolutorLinux(SOLVER);
			} else {
				this.currentCompiler = new MinizincResolutor(SOLVER);
			}
		} else {
			this.currentCompiler = new Resolutor();
		}
	}
	
	public Map<String,String> solve() {
		return this.currentCompiler.solve();
	}
	
	public List<Map<String,String>> solveGetAllSolutions() {
		return this.currentCompiler.solveGetAllSolutions(MAX_RESULTS);
	}
	
	public Integer getMaxResults() {
		return Integer.parseInt(MAX_RESULTS);
	}
	


}
