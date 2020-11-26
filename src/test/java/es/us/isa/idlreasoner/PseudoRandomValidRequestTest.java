package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PseudoRandomValidRequestTest {

    @AfterAll
    public static void killChildProcesses() {
        Analyzer.killChildProcesses();
    }

    @Test
    public void no_params() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/noParams", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: no_params.");
    }

    @Test
    public void one_param_boolean_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_param_boolean_no_deps.");
    }

    @Test
    public void one_param_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamString", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_param_string_no_deps.");
    }

    @Test
    public void one_param_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamInt", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_param_int_no_deps.");
    }

    @Test
    public void one_param_enum_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_param_enum_string_no_deps.");
    }

    @Test
    public void one_param_enum_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_requires() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_requires.");
    }

    @Test
    public void one_dep_or() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_or.");
    }

    @Test
    public void one_dep_onlyone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_onlyone.");
    }

    @Test
    public void one_dep_allornone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_allornone.");
    }

    @Test
    public void one_dep_zeroorone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_zeroorone.");
    }

    @Test
    public void one_dep_arithrel() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_arithrel.");
    }

    @Test
    public void one_dep_complex() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: one_dep_complex.");
    }

    @Test
    public void combinatorial1() {
        Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: combinatorial1.");
    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial2() {
//        Analyzer analyzer = new Analyzer("oas","combinatorial2.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial2", "get");
//        System.out.println("Test passed: combinatorial2.");
//    }

    @Test
    public void combinatorial3() {
        Analyzer analyzer = new Analyzer("oas","combinatorial3.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial3", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: combinatorial3.");
    }

    @Test
    public void combinatorial4() {
        Analyzer analyzer = new Analyzer("oas","combinatorial4.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial4", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: combinatorial4.");
    }

    @Test
    public void combinatorial5() {
        Analyzer analyzer = new Analyzer("oas","combinatorial5.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial5", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: combinatorial5.");
    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial6() {
//        Analyzer analyzer = new Analyzer("oas","combinatorial6.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial6", "get");
//        System.out.println("Test passed: combinatorial6.");
//    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial7() {
//        Analyzer analyzer = new Analyzer("oas","combinatorial7.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial7", "get");
//        System.out.println("Test passed: combinatorial7.");
//    }

    @Test
    public void combinatorial8() {
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");
        Map<String, String> validRequest = analyzer.getPseudoRandomValidRequest();
        // System.out.println(validRequest);
        assertTrue(analyzer.isValidRequest(validRequest), "The request should be VALID");
        System.out.println("Test passed: combinatorial8.");
    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial9() {
//        Analyzer analyzer = new Analyzer("oas","combinatorial9.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial9", "get");
//        System.out.println("Test passed: combinatorial9.");
//    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial10() {
//        Analyzer analyzer = new Analyzer("oas","combinatorial10.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial10", "get");
//        System.out.println("Test passed: combinatorial10.");
//    }
}
