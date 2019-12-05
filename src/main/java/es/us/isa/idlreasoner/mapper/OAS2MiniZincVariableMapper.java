package es.us.isa.idlreasoner.mapper;

import es.us.isa.idlreasoner.pojos.Variable;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.eclipse.emf.common.util.URI;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static es.us.isa.idlreasoner.util.FileManager.openWriter;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.CONSTRAINTS_FILE;

public class OAS2MiniZincVariableMapper extends AbstractVariableMapper {

    private OpenAPI openAPISpec;
    private List<Parameter> parameters;

    public OAS2MiniZincVariableMapper(String apiSpecificationPath, String operationPath, String operationType) {
        this.apiSpecificationPath = apiSpecificationPath;
        variables = new ArrayList<>();
        reservedWords = Arrays.asList("annotation","any", "array", "bool", "case", "diff",
                "div", "else", "elseif", "endif", "enum", "false", "float", "function", "if", "include",
                "intersect", "let", "list", "maximize", "minimize", "mod",  "of", "opt", "output", "par",
                "predicate", "record", "satisfy", "set", "solve", "string", "subset", "superset", "symdiff", "test",
                "then", "tuple", "type","union", "var", "where", "xor");

        openAPISpec = new OpenAPIV3Parser().read(apiSpecificationPath);
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

//        try {
////            recreateConstraintsFile();
//            mapVariables();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void mapVariables() throws IOException {
        variables.clear();
        List<String> previousContent = savePreviousFileContent();
        recreateFile(CONSTRAINTS_FILE);

        BufferedWriter out = openWriter(CONSTRAINTS_FILE);
        BufferedWriter requiredVarsOut = openWriter(CONSTRAINTS_FILE);
        String var;
        String varSet;

        for (Parameter parameter : parameters) {
            Schema<?> schema = parameter.getSchema();

            if(schema.getType().equals("boolean")) {
                var = "var bool: ";
            } else if(schema.getEnum() != null) {
                if (schema.getType().equals("string")) {
                    var = "var 0.." + (schema.getEnum().size()-1) + ": ";
                } else if (schema.getType().equals("integer")) {
                    var = "var {";
                    for (Object o : schema.getEnum()) {
                        var += o + ", ";
                    }
                    var = var.substring(0, var.length()-2);
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
    }

    private void mapRequiredVar(BufferedWriter requiredVarsOut, Parameter parameter) throws IOException {
        requiredVarsOut.append("constraint " + changeIfReservedWord(parameter.getName())+"Set = 1;\n");
    }
}
