package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.*;

public class AllRequestsTest {

    @Test
    public void no_params() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/noParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(new HashMap<>());
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: no_params.");
    }

    @Test
    public void one_param_boolean_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamBoolean", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(new HashMap<>());
        allRequests.add(ImmutableMap.of("p1", "true"));
        allRequests.add(ImmutableMap.of("p1", "false"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_boolean_no_deps.");
    }

    @Test
    public void one_param_enum_string_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumString", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(new HashMap<>());
        allRequests.add(ImmutableMap.of("p1", "value1"));
        allRequests.add(ImmutableMap.of("p1", "value2"));
        allRequests.add(ImmutableMap.of("p1", "value3"));
        allRequests.add(ImmutableMap.of("p1", "value4"));
        allRequests.add(ImmutableMap.of("p1", "value5"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_string_no_deps.");
    }

    @Test
    public void one_param_enum_int_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneParamEnumInt", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(new HashMap<>());
        allRequests.add(ImmutableMap.of("p1", "1"));
        allRequests.add(ImmutableMap.of("p1", "2"));
        allRequests.add(ImmutableMap.of("p1", "3"));
        allRequests.add(ImmutableMap.of("p1", "4"));
        allRequests.add(ImmutableMap.of("p1", "5"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_no_deps() {
        Analyzer analyzer = new Analyzer("oas","no_deps.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_requires() {
        Analyzer analyzer = new Analyzer("oas","one_dep_requires.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_or() {
        Analyzer analyzer = new Analyzer("oas","one_dep_or.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_onlyone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_onlyone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_allornone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_allornone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2", "p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_zeroorone() {
        Analyzer analyzer = new Analyzer("oas","one_dep_zeroorone.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_arithrel() {
        Analyzer analyzer = new Analyzer("oas","one_dep_arithrel.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyArithRelEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(ImmutableMap.of("p5", "1"));
        allRequests.add(ImmutableMap.of("p5", "2"));
        allRequests.add(ImmutableMap.of("p5", "3"));
        allRequests.add(ImmutableMap.of("p3", "1", "p5", "1"));
        allRequests.add(ImmutableMap.of("p3", "1", "p5", "2"));
        allRequests.add(ImmutableMap.of("p3", "2", "p5", "2"));
        allRequests.add(ImmutableMap.of("p3", "1", "p5", "3"));
        allRequests.add(ImmutableMap.of("p3", "2", "p5", "3"));
        allRequests.add(ImmutableMap.of("p3", "3", "p5", "3"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }

    @Test
    public void one_dep_enum_params_complex() {
        Analyzer analyzer = new Analyzer("oas","one_dep_complex.idl", "./src/test/resources/OAS_test_suite.yaml", "/oneDependencyComplexEnumParams", "get");
        List<Map<String, String>> allAnalyzerRequests = analyzer.getAllRequests();
        List<Map<String, String>> allRequests = new ArrayList<>();
        allRequests.add(new HashMap<>());
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p2", "value2"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "true", "p3", "2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p2", "value2"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "1"));
        allRequests.add(ImmutableMap.of("p1", "false", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value1", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "1"));
        allRequests.add(ImmutableMap.of("p2", "value2", "p3", "2"));
        allRequests.add(ImmutableMap.of("p2", "value1"));
        allRequests.add(ImmutableMap.of("p2", "value2"));
        allRequests.add(ImmutableMap.of("p3", "1"));
        allRequests.add(ImmutableMap.of("p3", "2"));
        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
        System.out.println("Test passed: one_param_enum_int_no_deps.");
    }
}
