package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YouTubeTest {

    @Test
    public void randomRequestTest() {
        Analyzer analyzer = new Analyzer("oas","foursquare.idl", "./src/test/resources/foursquare.yaml", "/venues/search", "get");
//        System.out.println(analyzer.randomRequest());
//        analyzer.getAllSetUpRequest().forEach(System.out::println);
    }
}
