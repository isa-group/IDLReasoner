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

//        recreateFile(BASE_CONSTRAINTS_FILE);
        initializeStringIntMapping();
//        initializeParameterNamesMapping();

//        BufferedWriter out = openWriter(BASE_CONSTRAINTS_FILE);
//        BufferedWriter requiredVarsOut = openWriter(BASE_CONSTRAINTS_FILE);
//        BufferedWriter dataOut = openWriter(DATA_FILE);

        StringBuilder currentVariables = new StringBuilder();
        variables = "";
        StringBuilder currentVariablesData = new StringBuilder();
        variablesData = "";
        requiredVarsConstraints = "";
        redundantSolutionsConstraints = "";
        baseProblem = "";
        redundantSolutionsConstraints += "%%% The following constraints are to avoid redundant solutions returned by MiniZinc %%%\n";

        for (Parameter parameter: parameters) {
            String paramType = ((AbstractSerializableParameter)parameter).getType();
            List<?> paramEnum = ((AbstractSerializableParameter) parameter).getEnum();
            String changedParamName = origToChangedParamName(parameter.getName());
            String var = "set of int: data_" + changedParamName + ";\n"
                       + "var data_" + changedParamName + ": " + changedParamName + ";\n";
            String varSet = "set of int: data_" + changedParamName + "Set;\n"
                          + "var data_" + changedParamName + "Set: " + changedParamName + "Set;\n";
            StringBuilder varData = new StringBuilder("data_" + changedParamName + " = ");
            String varSetData = "data_" + changedParamName + "Set = {0, 1};\n";

            if(paramType.equals("boolean")) {
                varData.append("{0, 1};\n");
                mapRedundantConstraint(changedParamName, "0");
            } else if(paramEnum != null) {
                varData.append("{");
                if (paramType.equals("string")) {
                    for (Object o : paramEnum) {
                        Integer intMapping = stringIntMapping.get(o.toString());
                        if (intMapping != null) {
                            varData.append(intMapping).append(", ");
                        } else {
                            stringIntMapping.put(o.toString(), stringToIntCounter);
                            varData.append(stringToIntCounter++).append(", ");
                        }
                    }
                    mapRedundantConstraint(changedParamName, Integer.toString(stringToIntCounter - 1));
                } else if (paramType.equals("integer")) {
                    for (Object o : paramEnum) {
                        varData.append(o).append(", ");
                    }
                    mapRedundantConstraint(parameter.getName(), paramEnum.get(0).toString());
//                } else if (schema.getType().equals("number")) {
//                    // TODO: Manage mapping of float enum
//                    var = "var float: ";
//                    mapPSetZero(parameter.getName(), "1");
                } else {
                    throw new IllegalArgumentException("The enum parameter type '" + paramType + "' is not allowed for IDLReasoner to work.");
                }
                varData = new StringBuilder(varData.substring(0, varData.length() - 2)); // trim last comma and space
                varData.append("};\n");
            } else if(paramType.equals("string") || paramType.equals("array")) {
                varData.append("0.." + MAX_STRING_INT_MAPPING + ";\n"); // If string or array, add enough possible values (MAX_STRING_INT_MAPPING)
                mapRedundantConstraint(parameter.getName(), "1");
            } else if (paramType.equals("integer") || paramType.equals("number")) {
                varData.append("-1000..1000;\n");
                mapRedundantConstraint(parameter.getName(), "1");
            } else {
                throw new IllegalArgumentException("The parameter type '" + paramType + "' is not allowed for IDLReasoner to work.");
            }

            // Save contents
            currentVariables.append(var);
            currentVariables.append(varSet);
            if (parameter.getRequired())
                mapRequiredVar(changedParamName);
            currentVariablesData.append(varData.toString());
            currentVariablesData.append(varSetData);


//            out.append(var);
//            out.append(varSet);
//            if (parameter.getRequired()) {
//                mapRequiredVar(requiredVarsOut, parameter);
//            }
//            dataOut.append(varData.toString());
//            dataOut.append(varSetData);

            operationParameters.put(parameter.getName(), new AbstractMap.SimpleEntry<>(paramType, parameter.getRequired()));
        }

        // Update MiniZinc fragments
        variables = currentVariables.toString();
        redundantSolutionsConstraints += "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n";
        baseProblem = variables + "\n" + idlConstraints + "\n" + requiredVarsConstraints;
        variablesData = currentVariablesData.toString();

        // Create MinZinc base file and data file
        appendContentToFile(BASE_CONSTRAINTS_FILE, baseProblem);
        appendContentToFile(DATA_FILE, variablesData);


//        out.newLine();
//        out.append(previousContent);
//
//        out.newLine();
//        requiredVarsOut.newLine();
//        redundantSolutionsConstraints += "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n";
//
//        out.flush();
//        requiredVarsOut.flush();
//        dataOut.flush();
//
//        out.close();
//        requiredVarsOut.close();
//        dataOut.close();

        exportStringIntMappingToFile();
//        exportParameterNamesMappingToFile();
        fixStringToIntCounter();
    }


//    private void mapRequiredVar(BufferedWriter requiredVarsOut, Parameter parameter) throws IOException {
//        requiredVarsOut.append("constraint " + origToChangedParamName(parameter.getName())+"Set = 1;\n");
//    }

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

    static void generateIDLfromIDL4OAS(String apiSpecificationPath, String operationPath, String operationType) {
        Swagger oasSpec = new SwaggerParser().read(apiSpecificationPath);
        Operation oasOp = getOasOperation(oasSpec, operationPath, operationType);

        List<String> IDLdeps = null;
        try {
            IDLdeps = (List<String>)oasOp.getVendorExtensions().get("x-dependencies");
        } catch (Exception e) {} // If the "x-dependencies" extension is not correctly used

        if (IDLdeps != null && IDLdeps.size() != 0) {
            String allDeps = String.join("\n", IDLdeps);
            appendContentToFile(IDL_AUX_FILE, allDeps);
        }
    }

}
