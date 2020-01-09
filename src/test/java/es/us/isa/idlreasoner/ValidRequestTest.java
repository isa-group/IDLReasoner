package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidRequestTest {

    @Test
    public void no_params_valid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/noParams", "get");
        Map<String, String> request = new HashMap<>();
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: no_params_valid.");
    }

//    // It is impossible to create an invalid request for an operation without parameters
//    @Test
//    public void no_params_invalid() {
//        System.out.println("Test passed: no_params_invalid.");
//    }

    @Test
    public void one_param_boolean_no_deps_valid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "false");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_param_boolean_no_deps_valid.");
    }

    @Test
    public void one_param_boolean_no_deps_invalid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "not boolean");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_param_boolean_no_deps_invalid.");
    }

    @Test
    public void one_param_string_no_deps_valid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamString", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "a string");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_param_string_no_deps_valid.");
    }

    @Test
    public void one_param_string_no_deps_invalid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamString", "get");
        Map<String, String> request = new HashMap<>();
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_param_string_no_deps_invalid.");
    }

    @Test
    public void one_param_int_no_deps_valid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamInt", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "10");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_param_int_no_deps_valid.");
    }

    @Test
    public void one_param_int_no_deps_invalid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamInt", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "not an integer");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_param_int_no_deps_invalid.");
    }

    @Test
    public void one_param_enum_string_no_deps_valid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "value1");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_param_enum_string_no_deps_valid.");
    }

    @Test
    public void one_param_enum_string_no_deps_invalid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "string not in enum alternatives");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_param_enum_string_no_deps_invalid.");
    }

    @Test
    public void one_param_enum_int_no_deps_valid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "1");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_param_enum_int_no_deps_valid.");
    }

    @Test
    public void one_param_enum_int_no_deps_invalid() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "6");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_param_enum_int_no_deps_invalid.");
    }

    @Test
    public void one_dep_requires_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p2", "a string");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_requires_valid.");
    }

    @Test
    public void one_dep_requires_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p3", "1");
        request.put("p4", "value1");
        request.put("p5", "2");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_requires_invalid.");
    }

    @Test
    public void one_dep_or_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_or_valid.");
    }

    @Test
    public void one_dep_or_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_or_invalid.");
    }

    @Test
    public void one_dep_onlyone_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "false");
        request.put("p3", "100");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_onlyone_valid.");
    }

    @Test
    public void one_dep_onlyone_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p3", "10");
        request.put("p5", "1");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_onlyone_invalid.");
    }

    @Test
    public void one_dep_allornone_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p3", "-5");
        request.put("p4", "value5");
        request.put("p5", "1");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_allornone_valid.");
    }

    @Test
    public void one_dep_allornone_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p3", "10");
        request.put("p4", "value5");
        request.put("p5", "1");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_allornone_invalid.");
    }

    @Test
    public void one_dep_zeroorone_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_zeroorone_valid.");
    }

    @Test
    public void one_dep_zeroorone_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p2", "1");
        request.put("p3", "1");
        request.put("p4", "1");
        request.put("p5", "1");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_zeroorone_invalid.");
    }

    @Test
    public void one_dep_arithrel_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p3", "1");
        request.put("p5", "1");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_arithrel_valid.");
    }

    @Test
    public void one_dep_arithrel_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p3", "2");
        request.put("p5", "1");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_arithrel_invalid.");
    }

    @Test
    public void one_dep_complex_valid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "false");
        request.put("p2", "string");
        request.put("p4", "value1");
        request.put("p5", "4");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: one_dep_complex_valid.");
    }

    @Test
    public void one_dep_complex_invalid() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "false");
        request.put("p2", "string");
        request.put("p3", "-1000");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: one_dep_complex_invalid.");
    }

    @Test
    public void combinatorial1_valid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "value1");
        request.put("p2", "value2");
        request.put("p3", "value3");
        request.put("p4", "value4");
        request.put("p5", "value1");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: combinatorial1_valid.");
    }

    @Test
    public void combinatorial1_invalid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "value1");
        request.put("p2", "value2");
        request.put("p3", "value3");
        request.put("p4", "value4");
        request.put("p5", "value5"); // Violates this dependency: p1==p5;
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: combinatorial1_invalid.");
    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial2_valid() {
//        System.out.println("Test passed: combinatorial2_valid.");
//    }
//
//    @Test
//    public void combinatorial2_invalid() {
//        System.out.println("Test passed: combinatorial2_invalid.");
//    }

    @Test
    public void combinatorial3_valid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial3.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial3", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p3", "value1");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: combinatorial3_valid.");
    }

    @Test
    public void combinatorial3_invalid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial3.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial3", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p3", "value4");
        request.put("p4", "1");
        request.put("p5", "3"); // Violates this dependency: p4>=p5;
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: combinatorial3_invalid.");
    }

    @Test
    public void combinatorial4_valid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial4.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial4", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p2", "1000000");
        request.put("p3", "100000");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: combinatorial4_valid.");
    }

    @Test
    public void combinatorial4_invalid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial4.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial4", "get");
        Map<String, String> request = new HashMap<>(); // An empty request violates multiple dependencies, for example: NOT ZeroOrOne(p5==1000, p4==10000 OR p3==100000, p2==1000000);
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: combinatorial4_invalid.");
    }

    @Test
    public void combinatorial5_valid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial5.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial5", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p2", "something");
        request.put("p3", "5");
        request.put("p4", "value5");
        request.put("p5", "-300");
        request.put("p6", "false");
        request.put("p7", "value5");
        request.put("p8", "1");
        request.put("p9", "value1");
        request.put("p10", "value2");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: combinatorial5_valid.");
    }

    @Test
    public void combinatorial5_invalid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial5.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial5", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "true");
        request.put("p2", "something");
        request.put("p3", "5");
        request.put("p4", "value5");
        request.put("p5", "-300");
        request.put("p6", "false"); // (See next comment)
        request.put("p7", "another example"); // p6 and p7 violate this dependency: Or(p1==p6, p4==p7);
        request.put("p8", "1");
        request.put("p9", "value1");
        request.put("p10", "value2");
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: combinatorial5_invalid.");
    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial6_valid() {
//        System.out.println("Test passed: combinatorial6_valid.");
//    }
//
//    @Test
//    public void combinatorial6_invalid() {
//        System.out.println("Test passed: combinatorial6_invalid.");
//    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial7_valid() {
//        System.out.println("Test passed: combinatorial7_valid.");
//    }
//
//    @Test
//    public void combinatorial7_invalid() {
//        System.out.println("Test passed: combinatorial7_invalid.");
//    }

    @Test
    public void combinatorial8_valid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "false");
        request.put("p2", "false");
        request.put("p4", "true");
        request.put("p5", "true");
        request.put("p6", "one string");
        request.put("p8", "something");
        request.put("p9", "fixed string");
        request.put("p10", "something");
        assertTrue(analyzer.validRequest(request), "The request should be VALID");
        System.out.println("Test passed: combinatorial8_valid.");
    }

    @Test
    public void combinatorial8_invalid() {
        Analyzer analyzer = new Analyzer("oas","combinatorial8.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial8", "get");
        Map<String, String> request = new HashMap<>();
        request.put("p1", "false");
        request.put("p2", "false");
        request.put("p4", "true");
        request.put("p5", "true");
        request.put("p6", "one string");
        request.put("p8", "something"); // (See next comment)
        request.put("p9", "fixed string");
        request.put("p10", "something different from p8"); // Violates this dependency: AllOrNone(p6!=p8, p8==p10);
        assertFalse(analyzer.validRequest(request), "The request should be NOT valid");
        System.out.println("Test passed: combinatorial8_invalid.");
    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial9_valid() {
//        System.out.println("Test passed: combinatorial9_valid.");
//    }
//
//    @Test
//    public void combinatorial9_invalid() {
//        System.out.println("Test passed: combinatorial9_invalid.");
//    }

//    // The operations whose IDL specification is invalid cannot be tested
//    @Test
//    public void combinatorial10_valid() {
//        System.out.println("Test passed: combinatorial10_valid.");
//    }
//
//    @Test
//    public void combinatorial10_invalid() {
//        System.out.println("Test passed: combinatorial10_invalid.");
//    }
}
