package es.us.isa.idlreasoner;


import es.us.isa.idlreasoner.analyzer.Analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class FullTest {

    @Test
    public void isFalseOptional() {
    	Analyzer analyzer = new Analyzer("oas","isValididl.idl", "./src/test/resources/OAS_example.yaml", "/requiredAndOptionalParams", "get");
        assertFalse(analyzer.isFalseOptional("p3"), "The param should be NOT false optional");
        assertFalse(analyzer.isFalseOptional("p4"), "The param should be NOT false optional");
    	System.out.println("Test false optional passed");
    	
    }
    
    @Test
    public void isValidIDL() {
    	Analyzer analyzer = new Analyzer("oas","isValididl.idl", "./src/test/resources/OAS_example.yaml", "/requiredAndOptionalParams", "get");
    	assertTrue(analyzer.isValidIDL(), "This IDL should be VALID");
		// assertTrue(analyzer.isSolvable(), "This IDL should be solvable");
    	System.out.println("Test valid IDL passed");
    }

    @Test
    public void randomRequest() {
        Analyzer analyzer = new Analyzer("oas","isValididl.idl", "./src/test/resources/OAS_example.yaml", "/requiredAndOptionalParams", "get");
        System.out.println(analyzer.randomRequest());
    }
    
//    @Test
//    public void isValidRequest() {
//    	Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_example.yaml", "/requiredAndOptionalParams", "get");
//    	analyzer.setParameter("p1", "1");
//    	analyzer.setParameter("p3", "1");
//    	analyzer.setParameter("p2", "1");
//    	assertTrue(analyzer.validRequest(), "This request should be a valid request");
//
//    	analyzer.setListParameterToVoid();
//    	analyzer.setParameter("p1", "1");
//    	assertFalse(analyzer.validRequest(), "This request should be a NOT valid request");
//
//    	System.out.println("Test valid request passed");
//    }
    
    
//    @Test
//    public void validPartialRequest() {
//    	Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_example.yaml", "/requiredAndOptionalParams", "get");
//    	analyzer.setParameter("p1", "1");
//    	assertTrue(analyzer.validPartialRequest(), "This request should be a valid partial request");
//
//    	analyzer.setListParameterToVoid();
//    	analyzer.setParameter("p2", "1");
//    	assertTrue(analyzer.validPartialRequest(), "This request should be a valid partial request");
//
//    	System.out.println("Test valid partial request passed");
//    }
    
    @Test
    public void numberOfRequest() {
    	Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_example.yaml", "/requiredAndOptionalParams", "get");
    	assertTrue(analyzer.numberOfRequest()==100);
    }
}
