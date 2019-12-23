package es.us.isa.idlreasoner.compiler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ResolutorCreator {
	
	private String osName;
	private String compiler;
	private String solver;
	private String fileRoute;
	private Resolutor currentCompiler;
	private String maxResults;
	
	
	public ResolutorCreator() {

		this.osName = System.getProperty("os.name");
		this.extractDataFromProperties();

		if (this.compiler.equals("Minizinc")) {
			if (this.osName.contains("Windows")) {
				this.currentCompiler = new MinizincResolutorWindows(this.solver);
			} else {
				this.currentCompiler = new MinizincResolutor(this.solver);
			}
		} else {
			this.currentCompiler = new Resolutor();
		}
	}
	
	public Map<String,String> solve() {
		return this.currentCompiler.solve();
	}
	
	public List<Map<String,String>> solveGetAllSolutions() {
		return this.currentCompiler.solveGetAllSolutions(this.maxResults);
	}

	
	private void extractDataFromProperties() {

		InputStream inputStream;
		
		try {
			Properties prop = new Properties();
			inputStream = new FileInputStream("./idl_aux_files/config.properties");
			prop.load(inputStream);

			this.compiler = prop.getProperty("compiler");
			this.solver= prop.getProperty("solver");
			this.fileRoute = prop.getProperty("fileRoute");
			this.maxResults = prop.getProperty("maxResults");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} 
		

	}
	
	public Integer getMaxResults() {
		return Integer.parseInt(this.maxResults);
	}
	


}
