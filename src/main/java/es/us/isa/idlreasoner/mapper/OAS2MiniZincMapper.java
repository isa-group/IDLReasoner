package es.us.isa.idlreasoner.mapper;

import es.us.isa.idlreasoner.util.CommonResources;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;

import java.io.*;
import java.util.*;

import static es.us.isa.idlreasoner.util.FileManager.*;

public class OAS2MiniZincMapper extends AbstractMapper {

    private List<Parameter> parameters;

    public OAS2MiniZincMapper(CommonResources cr, String apiSpecificationPath, String operationPath, String operationType) {
        super(cr);
        this.specificationPath = apiSpecificationPath;

        ParseOptions options = new ParseOptions();
        options.setResolveFully(true);
        OpenAPI openAPISpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        Operation operation = getOasOperation(openAPISpec, operationPath, operationType);
        parameters = operation.getParameters(); // NullPointerException would be thrown on purpose, to stop program
        if (operation.getRequestBody() != null) {
            if (parameters == null)
                parameters = new ArrayList<>();
            parameters.addAll(getFormDataParameters(operation));
        }
    }

    public void mapVariables() throws IOException {
        if (parameters == null || parameters.size() == 0)
            return;
        operationParameters.clear();

        initializeStringIntMappingWithIDLValues();

        StringBuilder currentVariables = new StringBuilder();
        variables = "";
        StringBuilder currentVariablesData = new StringBuilder();
        variablesData = "";
        requiredVarsConstraints = "";
        redundantSolutionsConstraints = "";
        baseProblem = "";
        redundantSolutionsConstraints += "%%% The following constraints are to avoid redundant solutions returned by MiniZinc %%%\n";

        for (Parameter parameter: parameters) {
            String paramType = parameter.getSchema().getType();
            List<?> paramEnum = parameter.getSchema().getEnum();
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
                    mapRedundantConstraint(changedParamName, paramEnum.get(0).toString());
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
                mapRedundantConstraint(changedParamName, "1");
            } else if (paramType.equals("integer") || paramType.equals("number")) {
                varData.append("-1000..1000;\n");
                mapRedundantConstraint(changedParamName, "1");
            } else {
                throw new IllegalArgumentException("The parameter type '" + paramType + "' is not allowed for IDLReasoner to work.");
            }

            // Save contents
            currentVariables.append(var);
            currentVariables.append(varSet);
            if (Boolean.TRUE.equals(parameter.getRequired()))
                mapRequiredVar(changedParamName);
            currentVariablesData.append(varData.toString());
            currentVariablesData.append(varSetData);

            operationParameters.put(parameter.getName(), new AbstractMap.SimpleEntry<>(paramType, Boolean.TRUE.equals(parameter.getRequired())));
        }

        // Update MiniZinc fragments
        variables = currentVariables.toString();
        redundantSolutionsConstraints += "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n";
        baseProblem = variables + "\n" + idlConstraints + "\n" + requiredVarsConstraints;
        variablesData = currentVariablesData.toString();

        // Create MinZinc base file and data file
        writeContentToFile(cr.BASE_CONSTRAINTS_FILE, baseProblem);
        writeContentToFile(cr.DATA_FILE, variablesData);
        writeContentToFile(cr.BASE_DATA_FILE, variablesData);

        exportStringIntMappingToFile();
        fixStringToIntCounter();
    }

    private static Operation getOasOperation(OpenAPI openAPISpec, String operationPath, String operationType) {
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

    private Collection<Parameter> getFormDataParameters(Operation operation) {
        List<Parameter> formDataParameters = new ArrayList<>();
        Schema formDataBody;
        Map<String, Schema> formDataBodyProperties;

        try {
            formDataBody = operation.getRequestBody().getContent().get("application/x-www-form-urlencoded").getSchema();
            formDataBodyProperties = formDataBody.getProperties();
        } catch (NullPointerException e) {
            return formDataParameters;
        }

        for (Map.Entry<String, Schema> property: formDataBodyProperties.entrySet()) {
            Parameter parameter = new Parameter().name(property.getKey()).in("formData").required(formDataBody.getRequired().contains(property.getKey()));
            parameter.setSchema(new Schema().type(property.getValue().getType()));
            parameter.getSchema().setEnum(property.getValue().getEnum());
            formDataParameters.add(parameter);
        }

        return formDataParameters;
    }
    
    public void generateIDLfromIDL4OAS(String apiSpecificationPath, String operationPath, String operationType) {
        ParseOptions options = new ParseOptions();
        options.setResolveFully(true);
        OpenAPI oasSpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        Operation oasOp = getOasOperation(oasSpec, operationPath, operationType);

        List<String> IDLdeps = null;
        try {
            IDLdeps = (List<String>)oasOp.getExtensions().get("x-dependencies");
        } catch (Exception e) {} // If the "x-dependencies" extension is not correctly used

        if (IDLdeps != null && IDLdeps.size() != 0) {
            String allDeps = String.join("\n", IDLdeps);
            appendContentToFile(cr.IDL_AUX_FILE, allDeps);
        }
    }

}
