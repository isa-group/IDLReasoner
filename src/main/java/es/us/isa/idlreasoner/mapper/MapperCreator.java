package es.us.isa.idlreasoner.mapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MapperCreator {
	
	private String compiler;
	
	private AbstractConstraintMapper constraintMapper;
	private AbstractVariableMapper variableMapper;
	
	public MapperCreator(String specificationType, String idl, String apiSpecificationPath, String operationPath, String operationType) {
		this.extractDataFromProperties();

		// ConstraintMapper: must be created BEFORE the VariableMapper
		if(this.compiler.toLowerCase().equals("minizinc"))
			this.constraintMapper = new MiniZincIDLConstraintMapper(idl);

		// VariableMapper: must be created AFTER the ConstraintMapper
		if(specificationType.toLowerCase().equals("oas") && this.compiler.toLowerCase().equals("minizinc"))
			this.variableMapper = new OAS2MiniZincVariableMapper(apiSpecificationPath, operationPath, operationType);
	}
	
	
	public AbstractConstraintMapper getConstraintMapper() {
		return this.constraintMapper;
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

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} 
		

	}

}
