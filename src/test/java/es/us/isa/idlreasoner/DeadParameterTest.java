package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeadParameterTest {

    @Test
    public void oneDepRequires() {
        Analyzer analyzer = new Analyzer("oneDepRequires.idl", "C:/Users/Alberto/workspace/IDL-Analyzer/src/test/resources/OAS_example.yaml", "get", "requiredAndOptionalParams");
        System.out.println(analyzer.isDeadParameter("p1"));
        System.out.println(analyzer.isDeadParameter("p2"));
        System.out.println(analyzer.isDeadParameter("p3"));
        System.out.println(analyzer.isDeadParameter("p4"));
    }
}
