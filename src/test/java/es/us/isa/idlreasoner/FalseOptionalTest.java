package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FalseOptionalTest {

    @Test
    public void no_params() {
        // If there are no parameters in the specification, isFalseOptional cannot be tested
        System.out.println("Test passed: no_params.");
    }

    @Test
    public void one_param_boolean_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_param_boolean_no_deps.");
    }

    @Test
    public void one_param_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamString", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_param_string_no_deps.");
    }

    @Test
    public void one_param_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamInt", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_param_int_no_deps.");
    }

    @Test
    public void one_param_enum_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_param_enum_string_no_deps.");
    }

    @Test
    public void one_param_enum_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_requires() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_requires.");
    }

    @Test
    public void one_dep_or() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_or.");
    }

    @Test
    public void one_dep_onlyone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_onlyone.");
    }

    @Test
    public void one_dep_allornone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_allornone.");
    }

    @Test
    public void one_dep_zeroorone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_zeroorone.");
    }

    @Test
    public void one_dep_arithrel() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_arithrel.");
    }

    @Test
    public void one_dep_complex() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        System.out.println("Test passed: one_dep_complex.");
    }

    @Test
    public void combinatorial1() {
        Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p2"), "The parameter p2 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p3"), "The parameter p3 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p4"), "The parameter p4 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p5"), "The parameter p5 should NOT be false optional");
        System.out.println("Test passed: combinatorial1.");
    }

    @Test
    public void combinatorial2() {
        Analyzer analyzer = new Analyzer("oas","combinatorial2.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial2", "get");
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
        System.out.println("Test passed: combinatorial2.");
    }

    @Test
    public void combinatorial3() {
        Analyzer analyzer = new Analyzer("oas","combinatorial3.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial3", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p2"), "The parameter p2 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p3"), "The parameter p3 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p4"), "The parameter p4 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p5"), "The parameter p5 should NOT be false optional");
        System.out.println("Test passed: combinatorial3.");
    }

    @Test
    public void combinatorial4() {
        Analyzer analyzer = new Analyzer("oas","combinatorial4.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial4", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p2"), "The parameter p2 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p3"), "The parameter p3 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p4"), "The parameter p4 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p5"), "The parameter p5 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p6"), "The parameter p6 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p7"), "The parameter p7 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p8"), "The parameter p8 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p9"), "The parameter p9 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p10"), "The parameter p10 should NOT be dead");
        System.out.println("Test passed: combinatorial4.");
    }

    @Test
    public void combinatorial9() {
        Analyzer analyzer = new Analyzer("oas","combinatorial9.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial9", "get");
        assertFalse(analyzer.isFalseOptional("p1"), "The parameter p1 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p2"), "The parameter p2 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p3"), "The parameter p3 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p4"), "The parameter p4 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p5"), "The parameter p5 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p6"), "The parameter p6 should NOT be false optional");
        assertTrue(analyzer.isFalseOptional("p7"), "The parameter p7 SHOULD be false optional");
        assertFalse(analyzer.isFalseOptional("p8"), "The parameter p8 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p9"), "The parameter p9 should NOT be false optional");
        assertFalse(analyzer.isFalseOptional("p10"), "The parameter p10 should NOT be false optional");
        System.out.println("Test passed: combinatorial9.");
    }
}
