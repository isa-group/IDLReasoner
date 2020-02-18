package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IDL4OASTest {

    @Test
    public void idl4oasTest() {
        Analyzer analyzer = new Analyzer("oas", "./src/test/resources/OAS_example.yaml", "/optionalParams", "get");
        assertTrue(analyzer.isValidIDL(), "The IDL should be VALID");
        System.out.println("Test passed: idl4oasTest.");
    }
}
