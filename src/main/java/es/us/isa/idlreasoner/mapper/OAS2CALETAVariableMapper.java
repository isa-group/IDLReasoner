package es.us.isa.idlreasoner.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import es.us.isa.IDL4OAS.PresenceParameter;
import es.us.isa.IDL4OAS.dependencies.RelationalObjectDependency;
import es.us.isa.idlreasoner.model.Parenthesis;
import es.us.isa.models.basics.BooleanDomain;
import es.us.isa.models.basics.Constant;
import es.us.isa.models.basics.Domain;
import es.us.isa.models.basics.IntegerDomain;
import es.us.isa.models.basics.NAryFunction;
import es.us.isa.models.basics.RealDomain;
import es.us.isa.models.basics.StringDomain;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OAS2CALETAVariableMapper implements CALETAVariableMapper {
	
	private String apiSpecificationPath;
	private String operationPath;
	private String operationType;
	
	private List<Parameter> parameters;
	private Map<String, es.us.isa.IDL4OAS.Parameter<?>> mapParameter;
	private Map<String, Boolean> mapRequiredParameter;
	private Collection<NAryFunction<Boolean>> dependencies;
	
	public OAS2CALETAVariableMapper(String apiSpecificationPath, String operationPath, String operationType) {
		this.apiSpecificationPath = apiSpecificationPath;
		this.operationPath = operationPath;
		this.operationType = operationType;
		
		this.parameters = new ArrayList<>();
		this.mapParameter = new HashMap<>();
		this.mapRequiredParameter = new HashMap<>();
		this.dependencies = new ArrayList<NAryFunction<Boolean>>();
		mapVariables();
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}

	public Map<String, es.us.isa.IDL4OAS.Parameter<?>> getMapParameter() {
		return mapParameter;
	}

	public Map<String, Boolean> getMapRequiredParameter() {
		return mapRequiredParameter;
	}

	public Collection<NAryFunction<Boolean>> getDependencies() {
		return dependencies;
	}


	public void mapVariables() {
		OpenAPI openAPISpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        Operation operation = getOasOperation(openAPISpec, operationPath, operationType);
        parameters = operation.getParameters(); // NullPointerException would be thrown on purpose, to stop program
        if (operation.getRequestBody() != null) {
            if (parameters == null)
                parameters = new ArrayList<>();
            parameters.addAll(getFormDataParameters(operation));
        }
        if(parameters!=null) {
            for (Parameter parameter : parameters) {
            	List<?> enumList = parameter.getSchema().getEnum();

            	Boolean isEnumerate = enumList!=null;
            	Boolean required = false;
            	Domain domain = null;
            	String parameterType = parameter.getSchema().getType();
            	
            	if(parameter.getRequired()!=null) {
            		required = parameter.getRequired();
            	}
            	
            	//TODO Array types
            	if("boolean".equals(parameterType)) {
        			domain = new BooleanDomain();
            	}else if("string".equals(parameterType)) {
            		if(isEnumerate) {
        				SortedSet<String> stringDomain = transformList2SortedSet((List<String>)enumList);
        				domain = new StringDomain(stringDomain);
        			}else {
        				domain = new StringDomain();
        			}
            	}else if("integer".equals(parameterType)) {
            		if(isEnumerate) {
         				SortedSet<Integer> integerDomain = transformList2SortedSet((List<Integer>)enumList);
        				domain = new IntegerDomain(integerDomain);
        			}else {
        				domain = new IntegerDomain();
        			}
            	}else if("number".equals(parameterType)) {
            		domain = new RealDomain();
            		//TODO Como no podemos obtener el tipo del array, hacemos una variable numérica.
            	}

            	es.us.isa.IDL4OAS.Parameter<?> p = new es.us.isa.IDL4OAS.Parameter<>(parameter.getName(), required, domain);
            	if(p.isRequired()) {
            		PresenceParameter pSet = new PresenceParameter(p.getName(), p, true);
            		Constant<Boolean> c = new Constant<Boolean>(true);
            		RelationalObjectDependency<Boolean> pSetRestriction = new RelationalObjectDependency<Boolean>(pSet, c, Boolean.class);
            		dependencies.add(pSetRestriction);
            	}
            	mapRequiredParameter.put(parameter.getName(), required);
            	mapParameter.put(parameter.getName(), p);
    		}
        }
		
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
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
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

   
   private <T> SortedSet<T> transformList2SortedSet(List<T> values){
   	SortedSet<T> res = new TreeSet<T>();
		for (T value: values) {
			res.add(value);
		}
		return res;
   	
   }
   
}
