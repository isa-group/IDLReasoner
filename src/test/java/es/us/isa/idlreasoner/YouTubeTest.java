package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YouTubeTest {

    @Test
    public void randomRequestTest() {
//        Analyzer analyzer = new Analyzer("oas","foursquare.idl", "./src/test/resources/foursquare.yaml", "/venues/search", "get");
        Analyzer analyzer2 = new Analyzer("oas","youtube_simplified.idl", "./src/test/resources/youtube_simplified.yaml", "/search", "get");
        System.out.println(analyzer2.randomRequest());
//        analyzer.getAllSetUpRequest().forEach(System.out::println);
    }
}
