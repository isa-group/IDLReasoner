package compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ResolutorCreator {
	
	private String osName;
	private String compiler;
	private String solver;
	private String fileRoute;
	private Resolutor curentCompiler;
	private String maxResults;
	
	
	public ResolutorCreator() {

		this.osName = System.getProperty("os.name");
		this.extractDataFromProperties();

		if(this.compiler.equals("Minizinc")) {	
			
			if(this.osName.contains("Windows")) {

				this.curentCompiler = new MinizincResolutorWindows(this.fileRoute, this.solver);
				
			}else{
				
				this.curentCompiler = new MinizincResolutor(this.fileRoute, this.solver);
			}
			

		}else {
			this.curentCompiler = new Resolutor(fileRoute);
		}

		
	}
	
	public Map<String,String> solve(String file) {
		return this.curentCompiler.solve(file);
	}
	
	public List<Map<String,String>>  solveGetAllSolutins(String file) {
		return this.curentCompiler.solveGetAllSolutins(file, this.maxResults);
	}

	
	private void extractDataFromProperties() {

		InputStream inputStream;
		
		try {
		
		Properties prop = new Properties();
		String propFileName = "config.properties";
		
		inputStream = new FileInputStream("./analyzerConfiguration/config.properties");
		
		//inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		
		prop.load(inputStream);
		
		this.compiler = prop.getProperty("compiler");
		this.solver= prop.getProperty("solver");
		this.fileRoute = prop.getProperty("fileRoute");
		this.maxResults = prop.getProperty("maxResults");

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} 
		

	}
	
	public String getDirectory() {
		return this.fileRoute;
	}
	
	public Integer getMaxResults() {
		return Integer.parseInt(this.maxResults);
	}
	


}
