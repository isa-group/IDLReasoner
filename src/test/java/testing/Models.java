package testing;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;
import static es.us.isa.idlreasoner.util.IDLConfiguration.initJosnFile;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import es.us.isa.idlreasoner.mapper.IDL2JSONMapper;
import es.us.isa.idlreasoner.model.Model;
import es.us.isa.idlreasoner.util.CommonResources;

public class Models {

	public static void main(String[] args) {
        //Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
	 	//System.out.println(analyzer.getAllRequests());
        Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
        System.out.println(analyzer.getAllRequests());
		/**
		CommonResources cr = new CommonResources();
		initJosnFile(cr);
		IDL2JSONMapper idl = new IDL2JSONMapper(cr, "./" + IDL_FILES_FOLDER + "/" + "combinatorial8.idl");
		idl.mapIDL2JSON();
		
		System.out.println(cr.IDL_JSON_FILE);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Model model =  mapper.readValue(new File(cr.IDL_JSON_FILE), Model.class);
			System.out.println(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

*/

	 	/**
	    analyzer = new Analyzer("oas","combinatorial2.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial2", "get");
	    System.out.println(analyzer.whyIsFalseOptional("p6"));
	    */
	}

}
