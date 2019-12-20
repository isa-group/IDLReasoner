package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YouTubeTest {

    @Test
    public void randomRequestTest() {
        Analyzer analyzer = new Analyzer("oas","youtube.idl", "./src/test/resources/youtube.yaml", "/search", "get");
        System.out.println(analyzer.randomRequest());
    }
}
