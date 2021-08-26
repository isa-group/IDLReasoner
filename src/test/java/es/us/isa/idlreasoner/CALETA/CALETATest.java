package es.us.isa.idlreasoner.CALETA;

import static es.us.isa.idlreasoner.util.IDLConfiguration.initFilesAndConf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import es.us.isa.idlreasoner.analyzer.CALETAnalyzer;
import es.us.isa.idlreasoner.analyzer.MinizincAnalyzer;
import es.us.isa.idlreasoner.compiler.CALETAResolutor;
import es.us.isa.idlreasoner.mapper.CALETAMapper;
import es.us.isa.idlreasoner.util.CommonResources;

public class CALETATest {

	public static void main(String[] args) {
			//AllRequestTest
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
	        System.out.println(allAnalyzerRequests);
	        allRequests.forEach(request -> assertTrue(allAnalyzerRequests.contains(request), "The following request is missing: " + request));
	        assertEquals(allAnalyzerRequests.size(), (new HashSet<>(allAnalyzerRequests).size()), "All requests should be different");
	        allAnalyzerRequests.forEach(analyzerRequest -> assertTrue(analyzer.isValidRequest(analyzerRequest), "The following request is not valid: " + analyzerRequest));
	        assertEquals(allAnalyzerRequests.size(), allRequests.size(), "The number of requests returned by getAllRequests() should be the same as the requests tested");
	        System.out.println("Test passed: one_param_enum_int_no_deps.");
	}

}
