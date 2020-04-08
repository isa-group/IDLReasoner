package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
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
        assertTrue(analyzer.validRequest(validRequest), "The request should be VALID");

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

        Map<String, String> validRandomRequest = analyzer.randomRequest();
        assertTrue(analyzer.validRequest(validRandomRequest), "The request should be VALID");

        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");

        Map<String, String> validPseudoRequest = analyzer.pseudoRandomRequest();
        assertTrue(analyzer.validRequest(validPseudoRequest), "The request should be VALID");

        Map<String, String> invalidRequest = new HashMap<>();
        invalidRequest.put("p1", "false");
        invalidRequest.put("p2", "false");
        invalidRequest.put("p4", "true");
        invalidRequest.put("p5", "true");
        invalidRequest.put("p6", "one string");
        invalidRequest.put("p8", "something"); // (See next comment)
        invalidRequest.put("p9", "fixed string");
        invalidRequest.put("p10", "something different from p8"); // Violates this dependency: AllOrNone(p6!=p8, p8==p10);
        assertFalse(analyzer.validRequest(invalidRequest), "The request should be NOT valid");

        Map<String, String> validRandomRequest2 = analyzer.randomRequest();
        assertTrue(analyzer.validRequest(validRandomRequest2), "The request should be VALID");

        Map<String, String> partiallyValidRequest = new HashMap<>();
        partiallyValidRequest.put("p1", "false");
        partiallyValidRequest.put("p2", "false");
        partiallyValidRequest.put("p4", "true");
        partiallyValidRequest.put("p6", "one string");
        partiallyValidRequest.put("p8", "something");
        partiallyValidRequest.put("p9", "fixed string");
        partiallyValidRequest.put("p10", "something");
        assertTrue(analyzer.validPartialRequest(partiallyValidRequest), "The partial request should be VALID");

        Map<String, String> partiallyInvalidRequest = new HashMap<>();
        partiallyInvalidRequest.put("p1", "false");
        partiallyInvalidRequest.put("p7", "a string"); // Violates this dependency: AllOrNone(p6!=p8, p8==p10);
        assertFalse(analyzer.validPartialRequest(partiallyInvalidRequest), "The partial request should be NOT valid");

        System.out.println("Test passed: multipleOperationsTest.");
    }

    @Test
    public void randomRequestCustomDataTest() {
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        List<String> p6Data = Arrays.asList("a", "b", "c", "d", "e");
        List<String> p7Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> p8Data = Arrays.asList("z", "f", "g", "h");
        List<String> p9Data = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> p10Data = Arrays.asList("z", "f", "g", "h");
        Map <String, List<String>> inputData = new HashMap<>();
        inputData.put("p6", p6Data);
        inputData.put("p7", p7Data);
        inputData.put("p8", p8Data);
        inputData.put("p9", p9Data);
        inputData.put("p10", p10Data);
        analyzer.updateData(inputData);
        Map<String, String> validRandomRequest = analyzer.pseudoRandomRequest();
        assertTrue(analyzer.validRequest(validRandomRequest), "The request should be VALID");
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
    public void customDataTwiceTest() {
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");

        List<String> p6Data = Arrays.asList("a", "f", "example");
        List<String> p7Data = Arrays.asList("b", "f");
        List<String> p8Data = Arrays.asList("c", "f", "something");
        List<String> p9Data = Arrays.asList("d", "f", "fixed string");
        List<String> p10Data = Arrays.asList("e", "f", "example");
        Map <String, List<String>> inputData = new HashMap<>();
        inputData.put("p6", p6Data);
        inputData.put("p7", p7Data);
        inputData.put("p8", p8Data);
        inputData.put("p9", p9Data);
        inputData.put("p10", p10Data);
        analyzer.updateData(inputData);
        Map<String, String> validRandomRequest = analyzer.randomRequest();

        List<String> p6Data2 = Arrays.asList("z", "u");
        List<String> p7Data2 = Arrays.asList("y", "u");
        List<String> p8Data2 = Arrays.asList("x", "u");
        List<String> p9Data2 = Arrays.asList("w", "u");
        List<String> p10Data2 = Arrays.asList("v", "u");
        Map <String, List<String>> inputData2 = new HashMap<>();
        inputData2.put("p6", p6Data2);
        inputData2.put("p7", p7Data2);
        inputData2.put("p8", p8Data2);
        inputData2.put("p9", p9Data2);
        inputData2.put("p10", p10Data2);
        analyzer.updateData(inputData2);
        Map<String, String> validRandomRequest2 = analyzer.randomRequest();

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
}
