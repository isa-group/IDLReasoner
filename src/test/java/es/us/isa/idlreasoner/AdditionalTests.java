package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import es.us.isa.idlreasoner.compiler.Resolutor;
import es.us.isa.idlreasoner.util.IDLConfiguration;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionalTests {

    @Test
    public void idl4oasTest() {
        Analyzer analyzer = new Analyzer("oas", "./src/test/resources/OAS_example.yaml", "/optionalParams", "get");
        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");
        System.out.println("Test passed: idl4oasTest.");
    }

    @Test
    public void formDataParametersTest() {
        Analyzer analyzer = new Analyzer("oas", "./src/test/resources/stripe.yaml", "/v1/products", "post");
        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");
        System.out.println("Test passed: formDataParametersTest.");
    }

    @Test
    public void formDataParametersV3Test() {
        Analyzer analyzer = new Analyzer("oas", "./src/test/resources/stripe_v3.yaml", "/v1/products", "post");
        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");
    }

    @Test
    public void validAfterInvalidRequest() {
        Analyzer analyzer = new Analyzer("oas","combinatorial5.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial5", "get");
        analyzer.getRandomInvalidRequest();
        Map<String, String> validRequest = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
    }

    @Test
    public void invalidAfterValidRequest() {
        Analyzer analyzer = new Analyzer("oas","combinatorial5.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial5", "get");
        analyzer.getRandomValidRequest();
        Map<String, String> invalidRequest = analyzer.getRandomInvalidRequest();
        assertFalse(analyzer.isValidRequest(invalidRequest), "The request should be NOT valid");
    }

    @Test
    public void multipleOperationsTest() {
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p2"), "The parameter p2 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p3"), "The parameter p3 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p4"), "The parameter p4 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p5"), "The parameter p5 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p6"), "The parameter p6 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p7"), "The parameter p7 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p8"), "The parameter p8 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p9"), "The parameter p9 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p10"), "The parameter p10 should NOT be dead");

        Map<String, String> validRequest = new HashMap<>();
        validRequest.put("p1", "false");
        validRequest.put("p2", "false");
        validRequest.put("p4", "true");
        validRequest.put("p5", "true");
        validRequest.put("p6", "one string");
        validRequest.put("p8", "something");
        validRequest.put("p9", "fixed string");
        validRequest.put("p10", "something");
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");

        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p2"), "The parameter p2 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p3"), "The parameter p3 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p4"), "The parameter p4 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p5"), "The parameter p5 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p6"), "The parameter p6 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p7"), "The parameter p7 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p8"), "The parameter p8 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p9"), "The parameter p9 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p10"), "The parameter p10 should NOT be false optional");

        Map<String, String> validRandomRequest = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest), "The request should be VALID");

        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");

        Map<String, String> invalidRandomRequest = analyzer.getRandomInvalidRequest();
        assertFalse(analyzer.isValidRequest(invalidRandomRequest), "The request should be NOT valid");

        Map<String, String> validPseudoRequest = analyzer.getPseudoRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validPseudoRequest), "The request should be VALID");

        Map<String, String> invalidRequest = new HashMap<>();
        invalidRequest.put("p1", "false");
        invalidRequest.put("p2", "false");
        invalidRequest.put("p4", "true");
        invalidRequest.put("p5", "true");
        invalidRequest.put("p6", "one string");
        invalidRequest.put("p8", "something"); // (See next comment)
        invalidRequest.put("p9", "fixed string");
        invalidRequest.put("p10", "something different from p8"); // Violates this dependency: AllOrNone(p6!=p8, p8==p10);
        assertFalse(analyzer.isValidRequest(invalidRequest), "The request should be NOT valid");

        Map<String, String> validRandomRequest2 = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest2), "The request should be VALID");

        Map<String, String> partiallyValidRequest = new HashMap<>();
        partiallyValidRequest.put("p1", "false");
        partiallyValidRequest.put("p2", "false");
        partiallyValidRequest.put("p4", "true");
        partiallyValidRequest.put("p6", "one string");
        partiallyValidRequest.put("p8", "something");
        partiallyValidRequest.put("p9", "fixed string");
        partiallyValidRequest.put("p10", "something");
        assertTrue(analyzer.isValidPartialRequest(partiallyValidRequest), "The partial request should be VALID");

        Map<String, String> partiallyInvalidRequest = new HashMap<>();
        partiallyInvalidRequest.put("p1", "false");
        partiallyInvalidRequest.put("p7", "a string"); // Violates this dependency: AllOrNone(p6!=p8, p8==p10);
        assertFalse(analyzer.isValidPartialRequest(partiallyInvalidRequest), "The partial request should be NOT valid");

        System.out.println("Test passed: multipleOperationsTest.");
    }

    @Test
    public void randomValidRequestCustomDataTest() {
    	org.junit.Assume.assumeTrue(!IDLConfiguration.ANALYZER.toLowerCase().equals("caleta"));
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        List<String> p1Data = Arrays.asList("true", "false");
        List<String> p2Data = Arrays.asList("true", "false");
        List<String> p3Data = Arrays.asList("true", "false");
        List<String> p4Data = Arrays.asList("true", "false");
        List<String> p5Data = Arrays.asList("true", "false");
        List<String> p6Data = Arrays.asList("a", "b", "c", "d", "e");
        List<String> p7Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> p8Data = Arrays.asList("z", "f", "g", "h");
        List<String> p9Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> p10Data = Arrays.asList("z", "f", "g", "h");
        Map <String, List<String>> inputData = new HashMap<>();
        inputData.put("p1", p1Data);
        inputData.put("p2", p2Data);
        inputData.put("p3", p3Data);
        inputData.put("p4", p4Data);
        inputData.put("p5", p5Data);
        inputData.put("p6", p6Data);
        inputData.put("p7", p7Data);
        inputData.put("p8", p8Data);
        inputData.put("p9", p9Data);
        inputData.put("p10", p10Data);
        analyzer.updateData(inputData);
        Map<String, String> validRandomRequest = analyzer.getPseudoRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest), "The request should be VALID");
        if (validRandomRequest.get("p6") != null)
            assertTrue(p6Data.contains(validRandomRequest.get("p6")));
        if (validRandomRequest.get("p7") != null)
            assertTrue(p7Data.contains(validRandomRequest.get("p7")));
        if (validRandomRequest.get("p8") != null)
            assertTrue(p8Data.contains(validRandomRequest.get("p8")));
        if (validRandomRequest.get("p9") != null)
            assertTrue(p9Data.contains(validRandomRequest.get("p9")));
        if (validRandomRequest.get("p10") != null)
            assertTrue(p10Data.contains(validRandomRequest.get("p10")));
    }
    

    @Test
    public void defaultAndNonDefaultDataIsValidRequestTest() {
        	org.junit.Assume.assumeTrue(!IDLConfiguration.ANALYZER.toLowerCase().equals("caleta"));
	        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");
	
	        Map<String, String> validRandomRequestBeforeUpdateData = analyzer.getRandomValidRequest();
	        assertTrue(analyzer.isValidRequest(validRandomRequestBeforeUpdateData), "The request should be VALID");
	
	        // Update data
	        List<String> p1Data = Arrays.asList("true", "false");
	        List<String> p2Data = Arrays.asList("true", "false");
	        List<String> p3Data = Arrays.asList("true", "false");
	        List<String> p4Data = Arrays.asList("true", "false");
	        List<String> p5Data = Arrays.asList("true", "false");
	        List<String> p6Data = Arrays.asList("a", "b", "c", "d", "e");
	        List<String> p7Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
	        List<String> p8Data = Arrays.asList("z", "f", "g", "h");
	        List<String> p9Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
	        List<String> p10Data = Arrays.asList("z", "f", "g", "h");
	        Map <String, List<String>> inputData = new HashMap<>();
	        inputData.put("p1", p1Data);
	        inputData.put("p2", p2Data);
	        inputData.put("p3", p3Data);
	        inputData.put("p4", p4Data);
	        inputData.put("p5", p5Data);
	        inputData.put("p6", p6Data);
	        inputData.put("p7", p7Data);
	        inputData.put("p8", p8Data);
	        inputData.put("p9", p9Data);
	        inputData.put("p10", p10Data);
	        analyzer.updateData(inputData);
	
	        assertFalse(analyzer.isValidRequest(validRandomRequestBeforeUpdateData), "The data was updated and, according to it, the request should be invalid");
	        assertTrue(analyzer.isValidRequest(validRandomRequestBeforeUpdateData, true), "With the default data, the request should be valid");
	
	        Map<String, String> validRandomRequestAfterUpdateData = analyzer.getPseudoRandomValidRequest();
	        assertTrue(analyzer.isValidRequest(validRandomRequestAfterUpdateData), "The request should be VALID");
	        assertTrue(analyzer.isValidRequest(validRandomRequestAfterUpdateData, true), "The request should be VALID");
	        if (validRandomRequestAfterUpdateData.get("p6") != null)
	            assertTrue(p6Data.contains(validRandomRequestAfterUpdateData.get("p6")));
	        if (validRandomRequestAfterUpdateData.get("p7") != null)
	            assertTrue(p7Data.contains(validRandomRequestAfterUpdateData.get("p7")));
	        if (validRandomRequestAfterUpdateData.get("p8") != null)
	            assertTrue(p8Data.contains(validRandomRequestAfterUpdateData.get("p8")));
	        if (validRandomRequestAfterUpdateData.get("p9") != null)
	            assertTrue(p9Data.contains(validRandomRequestAfterUpdateData.get("p9")));
	        if (validRandomRequestAfterUpdateData.get("p10") != null)
	            assertTrue(p10Data.contains(validRandomRequestAfterUpdateData.get("p10")));
    }

    @Test
    public void randomInvalidRequestCustomDataTest() {
    	org.junit.Assume.assumeTrue(!IDLConfiguration.ANALYZER.toLowerCase().equals("caleta"));
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        List<String> p1Data = Arrays.asList("true", "false");
        List<String> p2Data = Arrays.asList("true", "false");
        List<String> p3Data = Arrays.asList("true", "false");
        List<String> p4Data = Arrays.asList("true", "false");
        List<String> p5Data = Arrays.asList("true", "false");
        List<String> p6Data = Arrays.asList("a", "b", "c", "d", "e");
        List<String> p7Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> p8Data = Arrays.asList("z", "f", "g", "h");
        List<String> p9Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> p10Data = Arrays.asList("z", "f", "g", "h");
        Map <String, List<String>> inputData = new HashMap<>();
        inputData.put("p1", p1Data);
        inputData.put("p2", p2Data);
        inputData.put("p3", p3Data);
        inputData.put("p4", p4Data);
        inputData.put("p5", p5Data);
        inputData.put("p6", p6Data);
        inputData.put("p7", p7Data);
        inputData.put("p8", p8Data);
        inputData.put("p9", p9Data);
        inputData.put("p10", p10Data);
        analyzer.updateData(inputData);
        Map<String, String> invalidRandomRequest = analyzer.getRandomInvalidRequest();
        assertFalse(analyzer.isValidRequest(invalidRandomRequest), "The request should be NOT valid");
        if (invalidRandomRequest.get("p6") != null)
            assertTrue(p6Data.contains(invalidRandomRequest.get("p6")));
        if (invalidRandomRequest.get("p7") != null)
            assertTrue(p7Data.contains(invalidRandomRequest.get("p7")));
        if (invalidRandomRequest.get("p8") != null)
            assertTrue(p8Data.contains(invalidRandomRequest.get("p8")));
        if (invalidRandomRequest.get("p9") != null)
            assertTrue(p9Data.contains(invalidRandomRequest.get("p9")));
        if (invalidRandomRequest.get("p10") != null)
            assertTrue(p10Data.contains(invalidRandomRequest.get("p10")));
    }

    @Test
    public void customDataTwiceTestValidRequest() {
		org.junit.Assume.assumeTrue(!IDLConfiguration.ANALYZER.toLowerCase().equals("caleta"));
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        List<String> p1Data = Arrays.asList("true", "false");
        List<String> p2Data = Arrays.asList("true", "false");
        List<String> p3Data = Arrays.asList("true", "false");
        List<String> p4Data = Arrays.asList("true", "false");
        List<String> p5Data = Arrays.asList("true", "false");
        List<String> p6Data = Arrays.asList("a", "f", "example");
        List<String> p7Data = Arrays.asList("b", "f");
        List<String> p8Data = Arrays.asList("c", "f", "something");
        List<String> p9Data = Arrays.asList("d", "f", "fixed string");
        List<String> p10Data = Arrays.asList("e", "f", "example");
        Map <String, List<String>> inputData = new HashMap<>();
        inputData.put("p1", p1Data);
        inputData.put("p2", p2Data);
        inputData.put("p3", p3Data);
        inputData.put("p4", p4Data);
        inputData.put("p5", p5Data);
        inputData.put("p6", p6Data);
        inputData.put("p7", p7Data);
        inputData.put("p8", p8Data);
        inputData.put("p9", p9Data);
        inputData.put("p10", p10Data);
        analyzer.updateData(inputData);
        Map<String, String> validRandomRequest = analyzer.getRandomValidRequest();

        List<String> p1Data2 = Arrays.asList("true", "false");
        List<String> p2Data2 = Arrays.asList("true", "false");
        List<String> p3Data2 = Arrays.asList("true", "false");
        List<String> p4Data2 = Arrays.asList("true", "false");
        List<String> p5Data2 = Arrays.asList("true", "false");
        List<String> p6Data2 = Arrays.asList("z", "u");
        List<String> p7Data2 = Arrays.asList("y", "u");
        List<String> p8Data2 = Arrays.asList("x", "u");
        List<String> p9Data2 = Arrays.asList("w", "u");
        List<String> p10Data2 = Arrays.asList("v", "u");
        Map <String, List<String>> inputData2 = new HashMap<>();
        inputData2.put("p1", p1Data2);
        inputData2.put("p2", p2Data2);
        inputData2.put("p3", p3Data2);
        inputData2.put("p4", p4Data2);
        inputData2.put("p5", p5Data2);
        inputData2.put("p6", p6Data2);
        inputData2.put("p7", p7Data2);
        inputData2.put("p8", p8Data2);
        inputData2.put("p9", p9Data2);
        inputData2.put("p10", p10Data2);
        analyzer.updateData(inputData2);
        Map<String, String> validRandomRequest2 = analyzer.getRandomValidRequest();

        if (validRandomRequest.get("p6") != null || validRandomRequest2.get("p6") != null)
            assertNotEquals(validRandomRequest.get("p6"), validRandomRequest2.get("p6"));
        if (validRandomRequest.get("p7") != null || validRandomRequest2.get("p7") != null)
            assertNotEquals(validRandomRequest.get("p7"), validRandomRequest2.get("p7"));
        if (validRandomRequest.get("p8") != null || validRandomRequest2.get("p8") != null)
            assertNotEquals(validRandomRequest.get("p8"), validRandomRequest2.get("p8"));
        if (validRandomRequest.get("p9") != null || validRandomRequest2.get("p9") != null)
            assertNotEquals(validRandomRequest.get("p9"), validRandomRequest2.get("p9"));
        if (validRandomRequest.get("p10") != null || validRandomRequest2.get("p10") != null)
            assertNotEquals(validRandomRequest.get("p10"), validRandomRequest2.get("p10"));
    }

    @Test
    public void customDataTwiceTestInvalidRequest() {
    	org.junit.Assume.assumeTrue(!IDLConfiguration.ANALYZER.toLowerCase().equals("caleta"));
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        List<String> p1Data = Arrays.asList("true", "false");
        List<String> p2Data = Arrays.asList("true", "false");
        List<String> p3Data = Arrays.asList("true", "false");
        List<String> p4Data = Arrays.asList("true", "false");
        List<String> p5Data = Arrays.asList("true", "false");
        List<String> p6Data = Arrays.asList("a", "f", "example");
        List<String> p7Data = Arrays.asList("b", "f");
        List<String> p8Data = Arrays.asList("c", "f", "something");
        List<String> p9Data = Arrays.asList("d", "f", "fixed string");
        List<String> p10Data = Arrays.asList("e", "f", "example");
        Map <String, List<String>> inputData = new HashMap<>();
        inputData.put("p1", p1Data);
        inputData.put("p2", p2Data);
        inputData.put("p3", p3Data);
        inputData.put("p4", p4Data);
        inputData.put("p5", p5Data);
        inputData.put("p6", p6Data);
        inputData.put("p7", p7Data);
        inputData.put("p8", p8Data);
        inputData.put("p9", p9Data);
        inputData.put("p10", p10Data);
        analyzer.updateData(inputData);
        Map<String, String> invalidRandomRequest = analyzer.getRandomInvalidRequest();

        List<String> p1Data2 = Arrays.asList("true", "false");
        List<String> p2Data2 = Arrays.asList("true", "false");
        List<String> p3Data2 = Arrays.asList("true", "false");
        List<String> p4Data2 = Arrays.asList("true", "false");
        List<String> p5Data2 = Arrays.asList("true", "false");
        List<String> p6Data2 = Arrays.asList("z", "u");
        List<String> p7Data2 = Arrays.asList("y", "u");
        List<String> p8Data2 = Arrays.asList("x", "u");
        List<String> p9Data2 = Arrays.asList("w", "u");
        List<String> p10Data2 = Arrays.asList("v", "u");
        Map <String, List<String>> inputData2 = new HashMap<>();
        inputData2.put("p1", p1Data2);
        inputData2.put("p2", p2Data2);
        inputData2.put("p3", p3Data2);
        inputData2.put("p4", p4Data2);
        inputData2.put("p5", p5Data2);
        inputData2.put("p6", p6Data2);
        inputData2.put("p7", p7Data2);
        inputData2.put("p8", p8Data2);
        inputData2.put("p9", p9Data2);
        inputData2.put("p10", p10Data2);
        analyzer.updateData(inputData2);
        Map<String, String> invalidRandomRequest2 = analyzer.getRandomInvalidRequest();

        if (invalidRandomRequest.get("p6") != null || invalidRandomRequest2.get("p6") != null)
            assertNotEquals(invalidRandomRequest.get("p6"), invalidRandomRequest2.get("p6"));
        if (invalidRandomRequest.get("p7") != null || invalidRandomRequest2.get("p7") != null)
            assertNotEquals(invalidRandomRequest.get("p7"), invalidRandomRequest2.get("p7"));
        if (invalidRandomRequest.get("p8") != null || invalidRandomRequest2.get("p8") != null)
            assertNotEquals(invalidRandomRequest.get("p8"), invalidRandomRequest2.get("p8"));
        if (invalidRandomRequest.get("p9") != null || invalidRandomRequest2.get("p9") != null)
            assertNotEquals(invalidRandomRequest.get("p9"), invalidRandomRequest2.get("p9"));
        if (invalidRandomRequest.get("p10") != null || invalidRandomRequest2.get("p10") != null)
            assertNotEquals(invalidRandomRequest.get("p10"), invalidRandomRequest2.get("p10"));
    }

    @Test
    public void conflictiveParameterNamesTest1() {
        Analyzer analyzer = new Analyzer("oas","conflictiveParameterNames.idl", "./src/test/resources/OAS_test_suite.yaml", "/conflictiveParameterNames", "get");

        assertFalse(analyzer.isDeadParameter("type"), "The parameter type should NOT be dead");
        assertFalse(analyzer.isDeadParameter("constraint"), "The parameter constraint should NOT be dead");
        assertFalse(analyzer.isDeadParameter("with_underscore"), "The parameter with_underscore should NOT be dead");
        assertFalse(analyzer.isDeadParameter("Accept-Language"), "The parameter Accept-Language should NOT be dead");
        assertFalse(analyzer.isDeadParameter("index:set"), "The parameter index:set should NOT be dead");
        assertFalse(analyzer.isDeadParameter("something[one]"), "The parameter something[one] should NOT be dead");
        assertFalse(analyzer.isDeadParameter("something[two]"), "The parameter something[two] should NOT be dead");
        assertFalse(analyzer.isDeadParameter("b.b"), "The parameter b.b should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p9"), "The parameter p9 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p10"), "The parameter p10 should NOT be dead");

        Map<String, String> validRequest = new HashMap<>();
        validRequest.put("type", "false");
        validRequest.put("constraint", "false");
        validRequest.put("Accept-Language", "true");
        validRequest.put("index:set", "true");
        validRequest.put("something[one]", "one string");
        validRequest.put("b.b", "something");
        validRequest.put("p9", "fixed string");
        validRequest.put("p10", "something");
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");

        assertFalse(analyzer.isFalseOptional("type"), "The parameter type should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("constraint"), "The parameter constraint should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("with_underscore"), "The parameter with_underscore should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("Accept-Language"), "The parameter Accept-Language should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("index:set"), "The parameter index:set should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("something[one]"), "The parameter something[one] should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("something[two]"), "The parameter something[two] should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("b.b"), "The parameter b.b should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p9"), "The parameter p9 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p10"), "The parameter p10 should NOT be false optional");

        Map<String, String> validRandomRequest = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest), "The request should be VALID");

        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");

        Map<String, String> invalidRandomRequest = analyzer.getRandomInvalidRequest();
        assertFalse(analyzer.isValidRequest(invalidRandomRequest), "The request should be NOT valid");

        Map<String, String> validPseudoRequest = analyzer.getPseudoRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validPseudoRequest), "The request should be VALID");

        Map<String, String> invalidRequest = new HashMap<>();
        invalidRequest.put("type", "false");
        invalidRequest.put("constraint", "false");
        invalidRequest.put("Accept-Language", "true");
        invalidRequest.put("index:set", "true");
        invalidRequest.put("something[one]", "one string");
        invalidRequest.put("b.b", "something"); // (See next comment)
        invalidRequest.put("p9", "fixed string");
        invalidRequest.put("p10", "something different from b.b"); // Violates this dependency: AllOrNone(something[one]!=b.b, b.b==p10);
        assertFalse(analyzer.isValidRequest(invalidRequest), "The request should be NOT valid");

        Map<String, String> validRandomRequest2 = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest2), "The request should be VALID");

        Map<String, String> partiallyValidRequest = new HashMap<>();
        partiallyValidRequest.put("type", "false");
        partiallyValidRequest.put("constraint", "false");
        partiallyValidRequest.put("Accept-Language", "true");
        partiallyValidRequest.put("something[one]", "one string");
        partiallyValidRequest.put("b.b", "something");
        partiallyValidRequest.put("p9", "fixed string");
        partiallyValidRequest.put("p10", "something");
        assertTrue(analyzer.isValidPartialRequest(partiallyValidRequest), "The partial request should be VALID");

        Map<String, String> partiallyInvalidRequest = new HashMap<>();
        partiallyInvalidRequest.put("type", "false");
        partiallyInvalidRequest.put("something[two]", "a string"); // Violates this dependency: IF type THEN (with_underscore==true OR (NOT with_underscore)) AND NOT [something[two]] AND p9=='fixed string';
        assertFalse(analyzer.isValidPartialRequest(partiallyInvalidRequest), "The partial request should be NOT valid");

        System.out.println("Test passed: multipleOperationsTest.");
    }

    @Test
    public void conflictiveParameterNamesTest2() {
        Analyzer analyzer = new Analyzer("oas","conflictiveParameterNames2.idl", "./src/test/resources/OAS_test_suite.yaml", "/conflictiveParameterNames2", "get");

        assertFalse(analyzer.isDeadParameter("type"), "The parameter type should NOT be dead");
        assertFalse(analyzer.isDeadParameter("constraint"), "The parameter constraint should NOT be dead");
        assertFalse(analyzer.isDeadParameter("with_underscore"), "The parameter with_underscore should NOT be dead");
        assertFalse(analyzer.isDeadParameter("Accept-Language"), "The parameter Accept-Language should NOT be dead");
        assertFalse(analyzer.isDeadParameter("index:set"), "The parameter index:set should NOT be dead");
        assertFalse(analyzer.isDeadParameter("something[one]"), "The parameter something[one] should NOT be dead");
        assertFalse(analyzer.isDeadParameter("something[two]"), "The parameter something[two] should NOT be dead");
        assertFalse(analyzer.isDeadParameter("b.b"), "The parameter b.b should NOT be dead");
        assertFalse(analyzer.isDeadParameter("c.c"), "The parameter c.c should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p10"), "The parameter p10 should NOT be dead");

        Map<String, String> validRequest = new HashMap<>();
        validRequest.put("type", "false");
        validRequest.put("constraint", "false");
        validRequest.put("Accept-Language", "true");
        validRequest.put("index:set", "true");
        validRequest.put("something[one]", "one string");
        validRequest.put("b.b", "something");
        validRequest.put("c.c", "fixed string");
        validRequest.put("p10", "something");
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");

        assertFalse(analyzer.isFalseOptional("type"), "The parameter type should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("constraint"), "The parameter constraint should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("with_underscore"), "The parameter with_underscore should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("Accept-Language"), "The parameter Accept-Language should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("index:set"), "The parameter index:set should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("something[one]"), "The parameter something[one] should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("something[two]"), "The parameter something[two] should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("b.b"), "The parameter b.b should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("c.c"), "The parameter c.c should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p10"), "The parameter p10 should NOT be false optional");

        Map<String, String> validRandomRequest = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest), "The request should be VALID");

        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");

        Map<String, String> invalidRandomRequest = analyzer.getRandomInvalidRequest();
        assertFalse(analyzer.isValidRequest(invalidRandomRequest), "The request should be NOT valid");

        Map<String, String> validPseudoRequest = analyzer.getPseudoRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validPseudoRequest), "The request should be VALID");

        Map<String, String> invalidRequest = new HashMap<>();
        invalidRequest.put("type", "false");
        invalidRequest.put("constraint", "false");
        invalidRequest.put("Accept-Language", "true");
        invalidRequest.put("index:set", "true");
        invalidRequest.put("something[one]", "one string");
        invalidRequest.put("b.b", "something"); // (See next comment)
        invalidRequest.put("c.c", "fixed string");
        invalidRequest.put("p10", "something different from b.b"); // Violates this dependency: AllOrNone(something[one]!=b.b, b.b==p10);
        assertFalse(analyzer.isValidRequest(invalidRequest), "The request should be NOT valid");

        Map<String, String> validRandomRequest2 = analyzer.getRandomValidRequest();
        assertTrue(analyzer.isValidRequest(validRandomRequest2), "The request should be VALID");

        Map<String, String> partiallyValidRequest = new HashMap<>();
        partiallyValidRequest.put("type", "false");
        partiallyValidRequest.put("constraint", "false");
        partiallyValidRequest.put("Accept-Language", "true");
        partiallyValidRequest.put("something[one]", "one string");
        partiallyValidRequest.put("b.b", "something");
        partiallyValidRequest.put("c.c", "fixed string");
        partiallyValidRequest.put("p10", "something");
        assertTrue(analyzer.isValidPartialRequest(partiallyValidRequest), "The partial request should be VALID");

        Map<String, String> partiallyInvalidRequest = new HashMap<>();
        partiallyInvalidRequest.put("type", "false");
        partiallyInvalidRequest.put("something[two]", "a string"); // Violates this dependency: IF type THEN (with_underscore==true OR (NOT with_underscore)) AND NOT [something[two]] AND p9=='fixed string';
        assertFalse(analyzer.isValidPartialRequest(partiallyInvalidRequest), "The partial request should be NOT valid");

        System.out.println("Test passed: multipleOperationsTest.");
    }
}
