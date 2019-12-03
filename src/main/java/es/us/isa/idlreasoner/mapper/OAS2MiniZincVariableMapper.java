package es.us.isa.idlreasoner.mapper;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.eclipse.emf.common.util.URI;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class OAS2MiniZincVariableMapper extends AbstractVariableMapper {

    private OpenAPI openAPISpec;
    private List<Parameter> parameters;

    public OAS2MiniZincVariableMapper(String oasPath, String operationPath, String operationType) {
        reservedWords = Arrays.asList("annotation","any", "array", "bool", "case", "diff",
                "div", "else", "elseif", "endif", "enum", "false", "float", "function", "if", "include",
                "intersect", "let", "list", "maximize", "minimize", "mod",  "of", "opt", "output", "par",
                "predicate", "record", "satisfy", "set", "solve", "string", "subset", "superset", "symdiff", "test",
                "then", "tuple", "type","union", "var", "where", "xor");

        openAPISpec = new OpenAPIV3Parser().read(oasPath);
        if(operationType.equals("get"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getGet().getParameters();
        if(operationType.equals("delete"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getDelete().getParameters();
        if(operationType.equals("post"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getPost().getParameters();
        if(operationType.equals("put"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getPut().getParameters();
        if(operationType.equals("patch"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getPatch().getParameters();
        if(operationType.equals("head"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getHead().getParameters();
        if(operationType.equals("options"))
            parameters = openAPISpec.getPaths().get("/"+operationPath).getOptions().getParameters();

        try {
            mapVariables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void mapVariables() throws IOException {
        FileWriter fw = new FileWriter(constraintsFile);
        BufferedWriter out = new BufferedWriter(fw);
        String var;
        String varSet;

        for (Parameter parameter : parameters) {
            Schema<?> schema = parameter.getSchema();

            if(schema.getType().equals("boolean")) {
                var = "var bool: ";
            } else if(schema.getEnum() != null) {
                int numberOfEnumProperties = schema.getEnum().size()-1;
                var = "var 0.." + numberOfEnumProperties + ": ";
            } else if(schema.getType().equals("string")) {
                var = "var 0..10000: "; // If string, add enough possible values (10000)
            } else if(schema.getType().equals("number")) {
                // TODO: Manage numbers mapping to MiniZinc
                var = "var float: ";
            } else {
                // TODO: Manage every other type mapping to MiniZinc
                var = "var 0..1: ";
            }
            var += changeIfReservedWord(parameter.getName())+";\n";
            out.append(var);

            varSet = "var 0..1: " + changeIfReservedWord(parameter.getName())+"Set;\n";
            out.append(varSet);
        }

        out.flush();
        out.close();
    }
}
