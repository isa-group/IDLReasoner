package es.us.isa.idlreasoner;

import es.us.isa.idlreasoner.analyzer.Analyzer;
import es.us.isa.idlreasoner.mapper.MiniZincConstraintMapper;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeadParameterTest {

    @Test
    public void oneDepRequires() {
        Analyzer analyzer = new Analyzer("oneDepRequires.idl", "./src/test/resources/OAS_example.yaml", "/optionalParams", "get");
        System.out.println(analyzer.isFalseOptional("p1"));
        System.out.println(analyzer.isFalseOptional("p2"));
        System.out.println(analyzer.isFalseOptional("p3"));
        System.out.println(analyzer.isFalseOptional("p4"));
    }
}
