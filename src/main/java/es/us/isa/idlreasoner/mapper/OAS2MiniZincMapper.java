package es.us.isa.idlreasoner.mapper;

import io.swagger.models.Swagger;
import io.swagger.models.Operation;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;

import java.io.*;
import java.util.*;

import static es.us.isa.idlreasoner.util.FileManager.*;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.savePreviousBaseConstraintsFileContent;

public class OAS2MiniZincMapper extends AbstractMapper {

    private List<Parameter> parameters;

    public OAS2MiniZincMapper(String apiSpecificationPath, String operationPath, String operationType) {
        super();
        this.specificationPath = apiSpecificationPath;

        Swagger openAPISpec = new SwaggerParser().read(apiSpecificationPath);
        parameters = getOasOperation(openAPISpec, operationPath, operationType).getParameters(); // NullPointerException would be thrown on purpose, to stop program
    }

    public void mapVariables() throws IOException {
        if (parameters == null || parameters.size() == 0)
            return;
        operationParameters.clear();
        List<String> previousContent = savePreviousBaseConstraintsFileContent();
        recreateFile(BASE_CONSTRAINTS_FILE);
        initializeStringIntMapping();
        initializeParameterNamesMapping();

        BufferedWriter out = openWriter(BASE_CONSTRAINTS_FILE);
        BufferedWriter requiredVarsOut = openWriter(BASE_CONSTRAINTS_FILE);
        // In order for MiniZinc not to return duplicated solutions when a parameter is not set, we establish constraints for each parameter such as: pSet==0 -> p==0
        constraintsRedundantSolutions += "%%% The following constraints are to avoid redundant solutions returned by MiniZinc %%%\n";

        String var;
        String varSet;
        Integer intMapping;

        for (Parameter parameter : parameters) {
            String paramType = ((AbstractSerializableParameter)parameter).getType();
            List<?> paramEnum = ((AbstractSerializableParameter) parameter).getEnum();

            if(paramType.equals("boolean")) {
                var = "var 0..1: ";
                mapPSetZero(parameter.getName(), "0");
            } else if(paramEnum != null) {
                if (paramType.equals("string")) {
                    var = "var {";
                    for (Object o : paramEnum) {
                        intMapping = stringIntMapping.get(o.toString());
                        if (intMapping != null) {
                            var += intMapping + ", ";
                        } else {
                            stringIntMapping.put(o.toString(), stringToIntCounter);
                            var += stringToIntCounter++ + ", ";
                        }
                    }
                    var = var.substring(0, var.length()-2); // trim last comma and space
                    var += "}: ";
                    mapPSetZero(parameter.getName(), Integer.toString(stringToIntCounter-1));
                } else if (paramType.equals("integer")) {
                    var = "var {";
                    for (Object o : paramEnum) {
                        var += o + ", ";
                    }
                    var = var.substring(0, var.length()-2); // trim last comma and space
                    var += "}: ";
                    mapPSetZero(parameter.getName(), paramEnum.get(0).toString());
//                } else if (schema.getType().equals("number")) {
//                    // TODO: Manage mapping of float enum
//                    var = "var float: ";
//                    mapPSetZero(parameter.getName(), "1");
                } else {
                    throw new IllegalArgumentException("The enum parameter type '" + paramType + "' is not allowed for IDLReasoner to work.");
                }
            } else if(paramType.equals("string")) {
                var = "var 0.." + MAX_STRING_INT_MAPPING + ": "; // If string, add enough possible values (MAX_STRING_INT_MAPPING)
                mapPSetZero(parameter.getName(), "1");
            } else if(paramType.equals("integer")) {
                var = "var int: ";
                mapPSetZero(parameter.getName(), "1");
            } else if (paramType.equals("array")) {
                var = "var 0.." + MAX_STRING_INT_MAPPING + ": "; // If array, treat it as a string, add enough possible values (MAX_STRING_INT_MAPPING)
                mapPSetZero(parameter.getName(), "1");
            } else if (paramType.equals("number")) {
                var = "var int: ";
                mapPSetZero(parameter.getName(), "1");
            } else {
                throw new IllegalArgumentException("The parameter type '" + paramType + "' is not allowed for IDLReasoner to work.");
            }
            var += origToChangedParamName(parameter.getName())+";\n";
            out.append(var);

            varSet = "var 0..1: " + origToChangedParamName(parameter.getName())+"Set;\n";
            out.append(varSet);

            if (parameter.getRequired()) {
                mapRequiredVar(requiredVarsOut, parameter);
            }
            operationParameters.put(parameter.getName(), new AbstractMap.SimpleEntry<>(paramType, parameter.getRequired()));
        }

        out.newLine();
        for (String previousContentLine : previousContent) {
            out.append(previousContentLine + "\n");
        }

        out.newLine();
        requiredVarsOut.newLine();
        constraintsRedundantSolutions += "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n";

        out.flush();
        requiredVarsOut.flush();

        out.close();
        requiredVarsOut.close();

        exportStringIntMappingToFile();
        exportParameterNamesMappingToFile();
    }


    private void mapRequiredVar(BufferedWriter requiredVarsOut, Parameter parameter) throws IOException {
        requiredVarsOut.append("constraint " + origToChangedParamName(parameter.getName())+"Set = 1;\n");
    }

    private static Operation getOasOperation(Swagger openAPISpec, String operationPath, String operationType) {
        if(operationType.toLowerCase().equals("get"))
            return openAPISpec.getPaths().get(operationPath).getGet();
        if(operationType.toLowerCase().equals("delete"))
            return openAPISpec.getPaths().get(operationPath).getDelete();
        if(operationType.toLowerCase().equals("post"))
            return openAPISpec.getPaths().get(operationPath).getPost();
        if(operationType.toLowerCase().equals("put"))
            return openAPISpec.getPaths().get(operationPath).getPut();
        if(operationType.toLowerCase().equals("patch"))
            return openAPISpec.getPaths().get(operationPath).getPatch();
        if(operationType.toLowerCase().equals("head"))
            return openAPISpec.getPaths().get(operationPath).getHead();
        if(operationType.toLowerCase().equals("options"))
            return openAPISpec.getPaths().get(operationPath).getOptions();

        return null; // This should never happen
    }

    static void generateIDLfromIDL4OAS(String apiSpecificationPath, String operationPath, String operationType) throws IOException {
        Swagger oasSpec = new SwaggerParser().read(apiSpecificationPath);
        Operation oasOp = getOasOperation(oasSpec, operationPath, operationType);

        List<String> IDLdeps = null;
        try {
            IDLdeps = (List<String>)oasOp.getVendorExtensions().get("x-dependencies");
        } catch (Exception e) {} // If the "x-dependencies" extension is not correctly used

        if (IDLdeps != null && IDLdeps.size() != 0) {
            BufferedWriter out = openWriter(IDL_AUX_FILE);
            for (String IDLdep : IDLdeps) {
                out.append(IDLdep + "\n");
            }
            out.flush();
            out.close();
        }
    }

}
