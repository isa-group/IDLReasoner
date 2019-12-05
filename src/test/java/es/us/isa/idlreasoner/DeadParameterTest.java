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
    public void oneDepRequires() throws IOException {
        Analyzer analyzer = new Analyzer("oneDepRequires.idl", "./src/test/resources/OAS_example.yaml", "optionalParams", "get");
//        Analyzer analyzer = new Analyzer("oneDepRequires.idl", "C:/Users/Alberto/workspace/IDL-Analyzer/src/test/resources/OAS_example.yaml", "requiredAndOptionalParams", "get");
        System.out.println(analyzer.isDeadParameter("p1"));
        System.out.println(analyzer.isDeadParameter("p2"));
        System.out.println(analyzer.isDeadParameter("p3"));
        System.out.println(analyzer.isDeadParameter("p4"));
//        File file = new File("./hola/hola2/hola.txt");
//        if (!file.exists()) {
//            System.out.println(file.getParentFile().mkdirs());
//            System.out.println(file.createNewFile());//        }
//
//        List<String> lines = new ArrayList<>();
//
//        FileReader fr = new FileReader(file);
//        BufferedReader reader = new BufferedReader(fr);
//
//        String line = reader.readLine();
//        while(line!=null) {
//            lines.add(line);
//            line = reader.readLine();
//        }
//
//        FileWriter fw = new FileWriter(file);
//        BufferedWriter out = new BufferedWriter(fw);
//
//        out.append("Holaaa");
//
//        out.newLine();
//        for (String fileLine : lines) {
//            out.append(fileLine+"\n");
//        }
//
//        out.flush();
//        out.close();

//        System.out.println(file.delete());
//        System.out.println(file.getParentFile().mkdirs());
//        try {
//            System.out.println(file.createNewFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        file.delete();
//        file.getParentFile().mkdirs();
//        return file;
    }
}
