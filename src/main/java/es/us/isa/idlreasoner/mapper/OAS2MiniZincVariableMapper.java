package es.us.isa.idlreasoner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.isa.idlreasoner.pojos.Variable;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.*;
import java.util.*;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.BASE_CONSTRAINTS_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.MAPPING_FILE;

public class OAS2MiniZincVariableMapper extends AbstractVariableMapper {

    private OpenAPI openAPISpec;
    private List<Parameter> parameters;
    private Map<String, Integer> stringIntMapping;
    private Map<String, Map<String, Integer>> mappingParameters;
    private Integer stringToIntCounter;

    public OAS2MiniZincVariableMapper(String apiSpecificationPath, String operationPath, String operationType) {
        super();
        this.apiSpecificationPath = apiSpecificationPath;
        stringIntMapping = new HashMap<>();
        mappingParameters = new HashMap<String, Map<String,Integer>>();
        reservedWords = Arrays.asList("annotation","any", "array", "bool", "case", "diff",
                "div", "else", "elseif", "endif", "enum", "false", "float", "function", "if", "include",
                "intersect", "let", "list", "maximize", "minimize", "mod",  "of", "opt", "output", "par",
                "predicate", "record", "satisfy", "set", "solve", "string", "subset", "superset", "symdiff", "test",
                "then", "tuple", "type","union", "var", "where", "xor");

        openAPISpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        if(operationType.equals("get"))
            parameters = openAPISpec.getPaths().get(operationPath).getGet().getParameters();
        if(operationType.equals("delete"))
            parameters = openAPISpec.getPaths().get(operationPath).getDelete().getParameters();
        if(operationType.equals("post"))
            parameters = openAPISpec.getPaths().get(operationPath).getPost().getParameters();
        if(operationType.equals("put"))
            parameters = openAPISpec.getPaths().get(operationPath).getPut().getParameters();
        if(operationType.equals("patch"))
            parameters = openAPISpec.getPaths().get(operationPath).getPatch().getParameters();
        if(operationType.equals("head"))
            parameters = openAPISpec.getPaths().get(operationPath).getHead().getParameters();
        if(operationType.equals("options"))
            parameters = openAPISpec.getPaths().get(operationPath).getOptions().getParameters();

        try {
            mapVariables();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
////            recreateConstraintsFile();
//            mapVariables();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void mapVariables() throws IOException {
        if (parameters == null || parameters.size() == 0)
            return;
        variables.clear();
        List<String> previousContent = savePreviousBaseConstraintsFileContent();
        recreateFile(BASE_CONSTRAINTS_FILE);
        initializeStringIntMapping();

        BufferedWriter out = openWriter(BASE_CONSTRAINTS_FILE);
        BufferedWriter requiredVarsOut = openWriter(BASE_CONSTRAINTS_FILE);
        String var;
        String varSet;
        Integer intMapping;

        for (Parameter parameter : parameters) {
            Schema<?> schema = parameter.getSchema();

            if(schema.getType().equals("boolean")) {
                var = "var bool: ";
            } else if(schema.getEnum() != null) {
                if (schema.getType().equals("string")) {
//                    var = "var 0.." + (schema.getEnum().size()-1) + ": ";
                    var = "var {";
                    for (Object o : schema.getEnum()) {
                        intMapping = stringIntMapping.get(o.toString());
                        if (intMapping != null) {
                            var += intMapping + ", ";
                        } else {
                            stringIntMapping.put(o.toString(), stringToIntCounter);
                            var += stringToIntCounter++ + ", ";
                        }
                    }
                    mappingParameters.put(parameter.getName(), stringIntMapping);
                    var = var.substring(0, var.length()-2); // trim last comma and space
                    var += "}: ";
                } else if (schema.getType().equals("integer")) {
                    var = "var {";
                    for (Object o : schema.getEnum()) {
                        var += o + ", ";
                    }
                    var = var.substring(0, var.length()-2); // trim last comma and space
                    var += "}: ";
                } else {
                    // TODO: Manage mapping of float enum
                    var = "var float: ";
                }
            } else if(schema.getType().equals("string")) {
                var = "var 0..10000: "; // If string, add enough possible values (10000)
            } else if(schema.getType().equals("integer")) {
                var = "var int: ";
            } else {
                // TODO: Manage mapping of float
                var = "var float: ";
            }
            var += changeIfReservedWord(parameter.getName())+";\n";
            out.append(var);

            varSet = "var 0..1: " + changeIfReservedWord(parameter.getName())+"Set;\n";
            out.append(varSet);

            if (parameter.getRequired() != null && parameter.getRequired()) {
                mapRequiredVar(requiredVarsOut, parameter);
            }
            variables.add(new Variable(parameter.getName(), schema.getType(), parameter.getRequired()));
        }

        out.newLine();
        for (String previousContentLine : previousContent) {
            out.append(previousContentLine + "\n");
        }

        out.flush();
        requiredVarsOut.flush();
        out.close();
        requiredVarsOut.close();

        exportStringIntMappingToFile();
    }

    public Map<String, Map<String,Integer>> getMappingParameters(){
        return this.mappingParameters;
    }


    private void mapRequiredVar(BufferedWriter requiredVarsOut, Parameter parameter) throws IOException {
        requiredVarsOut.append("constraint " + changeIfReservedWord(parameter.getName())+"Set = 1;\n");
    }

//    private Integer stringToInt(String stringValue) {
//        Integer intMapping = stringIntMapping.get(stringValue);
//        if ((intMapping != null)) {
//            return intMapping;
//        } else {
//            this.stringIntMapping.put(stringValue, this.stringToIntCounter);
//            return this.stringToIntCounter++;
//        }
//    }

    private void initializeStringIntMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Integer>> typeRef = new TypeReference<HashMap<String, Integer>>() {};
        stringIntMapping = mapper.readValue(new File(MAPPING_FILE), typeRef);
        Map.Entry<String, Integer> entryWithHighestInt = stringIntMapping.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);
        if (entryWithHighestInt != null) {
            stringToIntCounter = entryWithHighestInt.getValue()+1;
        } else {
            stringToIntCounter = 0;
        }
    }

    private void exportStringIntMappingToFile() throws IOException {
        recreateFile(MAPPING_FILE);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stringIntMapping);
        appendContentToFile(MAPPING_FILE, json);
    }
}
