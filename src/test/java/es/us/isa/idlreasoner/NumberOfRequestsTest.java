package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberOfRequestsTest {

    @AfterAll
    public static void killChildProcesses() {
        Analyzer.killChildProcesses();
    }

    @Test
    public void no_params() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/noParams", "get");
        assertEquals(1, analyzer.numberOfRequest(), "The total number of requests should be equal to 1");
        System.out.println("Test passed: no_params.");
    }

    @Test
    public void one_param_boolean_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        assertEquals(3, analyzer.numberOfRequest(), "The total number of requests should be equal to 3");
        System.out.println("Test passed: one_param_boolean_no_deps.");
    }

    @Test
    public void one_param_enum_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        assertEquals(6, analyzer.numberOfRequest(), "The total number of requests should be equal to 6");
        System.out.println("Test passed: one_param_enum_string_no_deps.");
    }

    @Test
    public void one_param_enum_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        assertEquals(6, analyzer.numberOfRequest(), "The total number of requests should be equal to 6");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        assertEquals(18, analyzer.numberOfRequest(), "The total number of requests should be equal to 18");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_requires() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        assertEquals(14, analyzer.numberOfRequest(), "The total number of requests should be equal to 14");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_or() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        assertEquals(16, analyzer.numberOfRequest(), "The total number of requests should be equal to 16");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_onlyone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        assertEquals(8, analyzer.numberOfRequest(), "The total number of requests should be equal to 8");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_allornone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        assertEquals(10, analyzer.numberOfRequest(), "The total number of requests should be equal to 10");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_zeroorone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        assertEquals(10, analyzer.numberOfRequest(), "The total number of requests should be equal to 10");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_arithrel() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyArithRelEnumParams", "get");
        assertEquals(9, analyzer.numberOfRequest(), "The total number of requests should be equal to 9");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_complex() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyComplexEnumParams", "get");
        assertEquals(17, analyzer.numberOfRequest(), "The total number of requests should be equal to 17");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }
}
