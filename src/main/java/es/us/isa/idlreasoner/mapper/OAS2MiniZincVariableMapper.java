package es.us.isa.idlreasoner.mapper;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.*;
import java.util.*;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class OAS2MiniZincVariableMapper extends AbstractVariableMapper {

    private OpenAPI openAPISpec;
    private List<Parameter> parameters;

    public OAS2MiniZincVariableMapper(String apiSpecificationPath, String operationPath, String operationType, MapperResources mr) {
        super(mr);
        this.specificationPath = apiSpecificationPath;

        openAPISpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        parameters = getOasOperation(openAPISpec, operationPath, operationType).getParameters(); // NullPointerException would be thrown on purpose, to stop program

        try {
            mapVariables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mapVariables() throws IOException {
        if (parameters == null || parameters.size() == 0)
            return;
        mr.operationParameters.clear();
        List<String> previousContent = savePreviousBaseConstraintsFileContent();
        recreateFile(BASE_CONSTRAINTS_FILE);
        initializeStringIntMapping();
        initializeParameterNamesMapping();

        BufferedWriter out = openWriter(BASE_CONSTRAINTS_FILE);
        BufferedWriter requiredVarsOut = openWriter(BASE_CONSTRAINTS_FILE);
        // In order for MiniZinc not to return duplicated solutions when a parameter is not set, we establish constraints for each parameter such as: pSet==0 -> p==0
        BufferedWriter pSetZeroOut = openWriter(BASE_CONSTRAINTS_FILE);
        pSetZeroOut.append("%%% The following constraints are to avoid redundant solutions returned by MiniZinc %%%\n");
        String var;
        String varSet;
        Integer intMapping;

        for (Parameter parameter : parameters) {
            Schema<?> schema = parameter.getSchema();

            if(schema.getType().equals("boolean")) {
                var = "var bool: ";
                mapPSetZero(pSetZeroOut, parameter.getName(), "false");
            } else if(schema.getEnum() != null) {
                if (schema.getType().equals("string")) {
                    var = "var {";
                    for (Object o : schema.getEnum()) {
                        intMapping = mr.stringIntMapping.get(o.toString());
                        if (intMapping != null) {
                            var += intMapping + ", ";
                        } else {
                            mr.stringIntMapping.put(o.toString(), mr.stringToIntCounter);
                            var += mr.stringToIntCounter++ + ", ";
                        }
                    }
                    var = var.substring(0, var.length()-2); // trim last comma and space
                    var += "}: ";
                    mapPSetZero(pSetZeroOut, parameter.getName(), Integer.toString(mr.stringToIntCounter-1));
                } else if (schema.getType().equals("integer")) {
                    var = "var {";
                    for (Object o : schema.getEnum()) {
                        var += o + ", ";
                    }
                    var = var.substring(0, var.length()-2); // trim last comma and space
                    var += "}: ";
                    mapPSetZero(pSetZeroOut, parameter.getName(), schema.getEnum().get(0).toString());
                } else {
                    // TODO: Manage mapping of float enum
                    var = "var float: ";
                    mapPSetZero(pSetZeroOut, parameter.getName(), "1");
                }
            } else if(schema.getType().equals("string")) {
                var = "var 0..10000: "; // If string, add enough possible values (10000)
                mapPSetZero(pSetZeroOut, parameter.getName(), "1");
            } else if(schema.getType().equals("integer")) {
                var = "var int: ";
                mapPSetZero(pSetZeroOut, parameter.getName(), "1");
            } else {
                // TODO: Manage mapping of float
                var = "var float: ";
                mapPSetZero(pSetZeroOut, parameter.getName(), "1");
            }
            var += origToChangedParamName(parameter.getName())+";\n";
            out.append(var);

            varSet = "var 0..1: " + origToChangedParamName(parameter.getName())+"Set;\n";
            out.append(varSet);

            if (parameter.getRequired() != null && parameter.getRequired()) {
                mapRequiredVar(requiredVarsOut, parameter);
            }
            mr.operationParameters.put(parameter.getName(), new AbstractMap.SimpleEntry<>(schema.getType(), parameter.getRequired()!=null ? parameter.getRequired() : false));
        }

        out.newLine();
        for (String previousContentLine : previousContent) {
            out.append(previousContentLine + "\n");
        }

        out.newLine();
        requiredVarsOut.newLine();
        pSetZeroOut.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        pSetZeroOut.newLine();

        out.flush();
        requiredVarsOut.flush();
        pSetZeroOut.flush();

        out.close();
        requiredVarsOut.close();
        pSetZeroOut.close();

        exportStringIntMappingToFile();
        exportParameterNamesMappingToFile();
    }


    private void mapRequiredVar(BufferedWriter requiredVarsOut, Parameter parameter) throws IOException {
        requiredVarsOut.append("constraint " + origToChangedParamName(parameter.getName())+"Set = 1;\n");
    }

    private void mapPSetZero(BufferedWriter pSetZeroOut, String paramName, String paramValue) throws IOException {
        pSetZeroOut.append("constraint ((" + origToChangedParamName(paramName) + "Set==0) -> (" + origToChangedParamName(paramName) + "==" + paramValue + "));\n");
    }

    private static Operation getOasOperation(OpenAPI openAPISpec, String operationPath, String operationType) {
        if(operationType.equals("get"))
            return openAPISpec.getPaths().get(operationPath).getGet();
        if(operationType.equals("delete"))
            return openAPISpec.getPaths().get(operationPath).getDelete();
        if(operationType.equals("post"))
            return openAPISpec.getPaths().get(operationPath).getPost();
        if(operationType.equals("put"))
            return openAPISpec.getPaths().get(operationPath).getPut();
        if(operationType.equals("patch"))
            return openAPISpec.getPaths().get(operationPath).getPatch();
        if(operationType.equals("head"))
            return openAPISpec.getPaths().get(operationPath).getHead();
        if(operationType.equals("options"))
            return openAPISpec.getPaths().get(operationPath).getOptions();

        return null; // This should never happen
    }

    public static void generateIDLfromIDL4OAS(String apiSpecificationPath, String operationPath, String operationType) throws IOException {
        OpenAPI oasSpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        Operation oasOp = getOasOperation(oasSpec, operationPath, operationType);

        List<String> IDLdeps = (List<String>)oasOp.getExtensions().get("x-dependencies");
        if (IDLdeps != null) {
            BufferedWriter out = openWriter(IDL_AUX_FILE);
            for (String IDLdep : IDLdeps) {
                out.append(IDLdep + "\n");
            }
            out.flush();
            out.close();
        }
    }

}
