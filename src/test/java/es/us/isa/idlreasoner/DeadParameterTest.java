package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DeadParameterTest {

//    // If there are no parameters in the specification, isDeadParameter cannot be tested
//    @Test
//    public void no_params() {
//        System.out.println("Test passed: no_params.");
//    }

    @Test
    public void one_param_boolean_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_param_boolean_no_deps.");
    }

    @Test
    public void one_param_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamString", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_param_string_no_deps.");
    }

    @Test
    public void one_param_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamInt", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_param_int_no_deps.");
    }

    @Test
    public void one_param_enum_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_param_enum_string_no_deps.");
    }

    @Test
    public void one_param_enum_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_requires() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_requires.");
    }

    @Test
    public void one_dep_or() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_or.");
    }

    @Test
    public void one_dep_onlyone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_onlyone.");
    }

    @Test
    public void one_dep_allornone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_allornone.");
    }

    @Test
    public void one_dep_zeroorone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_zeroorone.");
    }

    @Test
    public void one_dep_arithrel() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_arithrel.");
    }

    @Test
    public void one_dep_complex() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependency", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        System.out.println("Test passed: one_dep_complex.");
    }

    @Test
    public void combinatorial1() {
        Analyzer analyzer = new Analyzer("oas","combinatorial1.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial1", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p2"), "The parameter p2 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p3"), "The parameter p3 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p4"), "The parameter p4 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p5"), "The parameter p5 should NOT be dead");
        System.out.println("Test passed: combinatorial1.");
    }

    @Test
    public void combinatorial2() {
        Analyzer analyzer = new Analyzer("oas","combinatorial2.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial2", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        assertTrue(analyzer.isDeadParameter("p2"), "The parameter p2 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p3"), "The parameter p3 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p4"), "The parameter p4 SHOULD be dead");
        assertFalse(analyzer.isDeadParameter("p5"), "The parameter p5 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p6"), "The parameter p6 should NOT be dead");
        assertTrue(analyzer.isDeadParameter("p7"), "The parameter p7 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p8"), "The parameter p8 SHOULD be dead");
        assertFalse(analyzer.isDeadParameter("p9"), "The parameter p9 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p10"), "The parameter p10 should NOT be dead");
        System.out.println("Test passed: combinatorial2.");
    }

    @Test
    public void combinatorial3() {
        Analyzer analyzer = new Analyzer("oas","combinatorial3.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial3", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p2"), "The parameter p2 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p3"), "The parameter p3 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p4"), "The parameter p4 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p5"), "The parameter p5 should NOT be dead");
        System.out.println("Test passed: combinatorial3.");
    }

    @Test
    public void combinatorial4() {
        Analyzer analyzer = new Analyzer("oas","combinatorial4.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial4", "get");
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
        System.out.println("Test passed: combinatorial4.");
    }

    @Test
    public void combinatorial5() {
        Analyzer analyzer = new Analyzer("oas","combinatorial5.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial5", "get");
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
        System.out.println("Test passed: combinatorial5.");
    }

    @Test
    public void combinatorial6() {
        Analyzer analyzer = new Analyzer("oas","combinatorial6.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial6", "get");
        assertTrue(analyzer.isDeadParameter("p1"), "The parameter p1 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p2"), "The parameter p2 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p3"), "The parameter p3 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p4"), "The parameter p4 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p5"), "The parameter p5 SHOULD be dead");
        System.out.println("Test passed: combinatorial6.");
    }

    @Test
    public void combinatorial7() {
        Analyzer analyzer = new Analyzer("oas","combinatorial7.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial7", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p2"), "The parameter p2 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p3"), "The parameter p3 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p4"), "The parameter p4 should NOT be dead");
        assertTrue(analyzer.isDeadParameter("p5"), "The parameter p5 SHOULD be dead");
        System.out.println("Test passed: combinatorial7.");
    }

    @Test
    public void combinatorial8() {
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
        System.out.println("Test passed: combinatorial8.");
    }

    @Test
    public void combinatorial9() {
        Analyzer analyzer = new Analyzer("oas","combinatorial9.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial9", "get");
        assertFalse(analyzer.isDeadParameter("p1"), "The parameter p1 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p2"), "The parameter p2 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p3"), "The parameter p3 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p4"), "The parameter p4 should NOT be dead");
        assertTrue(analyzer.isDeadParameter("p5"), "The parameter p5 SHOULD be dead");
        assertFalse(analyzer.isDeadParameter("p6"), "The parameter p6 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p7"), "The parameter p7 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p8"), "The parameter p8 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p9"), "The parameter p9 should NOT be dead");
        assertFalse(analyzer.isDeadParameter("p10"), "The parameter p10 should NOT be dead");
        System.out.println("Test passed: combinatorial9.");
    }

    @Test
    public void combinatorial10() {
        Analyzer analyzer = new Analyzer("oas","combinatorial10.idl", "./src/test/resources/OAS_test_suite.yaml", "/combinatorial10", "get");
        assertTrue(analyzer.isDeadParameter("p1"), "The parameter p1 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p2"), "The parameter p2 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p3"), "The parameter p3 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p4"), "The parameter p4 SHOULD be dead");
        assertTrue(analyzer.isDeadParameter("p5"), "The parameter p5 SHOULD be dead");
        System.out.println("Test passed: combinatorial10.");
    }
}
