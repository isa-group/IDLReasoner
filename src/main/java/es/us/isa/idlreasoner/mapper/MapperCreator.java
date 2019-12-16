package es.us.isa.idlreasoner.mapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MapperCreator {
	
	private String compiler;
	private String specification;
	private String mapper;
	
	private AbstractConstraintMapper contraintMapper;
	private AbstractVariableMapper variableMapper;
	
	public MapperCreator(String idl, String apiSpecificationPath, String operationPath, String operationType) {
		this.extractDataFromProperties();
		
		//VariableMapper
		
		if(this.specification.toLowerCase().equals("oas") && this.compiler.toLowerCase().equals("minizinc")) {
			this.variableMapper = new OAS2MiniZincVariableMapper(apiSpecificationPath, operationPath, operationType);
		}
		
		//ConstraintMapper
		
		if(this.compiler.toLowerCase().equals("minizinc") && this.mapper.equals("idl")) {
			this.contraintMapper = new MiniZincIDLConstraintMapper(idl);
		}
	}
	
	
	public AbstractConstraintMapper getContraintMapper() {
		return this.contraintMapper;
	}
	
	public AbstractVariableMapper getVariableMapper() {
		return this.variableMapper;
	}
	
	
	
	private void extractDataFromProperties() {

		InputStream inputStream;
		
		try {
		
		Properties prop = new Properties();
		
		inputStream = new FileInputStream("./idl_aux_files/config.properties");
		
		prop.load(inputStream);
		
		this.compiler = prop.getProperty("compiler");
		this.specification= prop.getProperty("specification");
		this.mapper = prop.getProperty("mapper");

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} 
		

	}

}
