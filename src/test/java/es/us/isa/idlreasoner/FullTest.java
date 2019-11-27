package es.us.isa.idlreasoner;


import es.us.isa.idlreasoner.analyzer.Analyzer;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class FullTest {

    @Test
    public void fullTest() {
        Analyzer a = new Analyzer("youtube_getVideos.idl",
                "https://api.apis.guru/v2/specs/googleapis.com/youtube/v3/swagger.yaml", "get", "videos");

        //GetAllRequests
        List<Map<String,String>> allRequests = a.getAllRequest();
        System.out.println("All requests: " );
        allRequests.forEach(r->System.out.println(r + "\n -------------"));

        //GetRandomRequest
        Map<String,String> request = a.randomRequest();
        System.out.println("Randon Request: " + request);

        //IsDeadParameter
        Boolean isDeadParameter = a.isDeadParameter("alt");
        System.out.println("Is dead Parameter 'alt': " + isDeadParameter);

        //IsFalseOptional
        Boolean isFalseOptional = a.isFalseOptional("alt");
        System.out.println("Is False optional 'alt': " + isFalseOptional);

        //IsValidIDL
        Boolean isValidIDL = a.isValidIDL();
        System.out.println("Is valid IDL: " + isValidIDL);

        //NumberOfRequest
        Integer numberOfRequest = a.numberOfRequest();
        System.out.println("Number of requests: " + numberOfRequest);

        //ValidRequest
        a.setParameter("videoType", "movie");
        a.setParameter("alt", "json");

        System.out.println("Is valid request : " + a.validRequest());

        //ValidPartialRequest
        a.setListParameterToVoid();
        a.setParameter("videoType", "movie");
        a.setParameter("alt", "json");

        System.out.println("Is valid partial request: " + a.validPartialRequest());
    }
}
