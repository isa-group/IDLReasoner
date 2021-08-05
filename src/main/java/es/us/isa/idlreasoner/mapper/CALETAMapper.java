package es.us.isa.idlreasoner.mapper;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;
import static es.us.isa.idlreasoner.util.IDLConfiguration.initJosnFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.isa.IDL4OAS.PresenceParameter;
import es.us.isa.IDL4OAS.dependencies.AllOrNoneDependency;
import es.us.isa.IDL4OAS.dependencies.IfThenDependency;
import es.us.isa.IDL4OAS.dependencies.OnlyOneDependency;
import es.us.isa.IDL4OAS.dependencies.OrDependency;
import es.us.isa.IDL4OAS.dependencies.RelationalObjectDependency;
import es.us.isa.IDL4OAS.dependencies.ZeroOrOneDependency;
import es.us.isa.idlreasoner.model.AllOrNone;
import es.us.isa.idlreasoner.model.And;
import es.us.isa.idlreasoner.model.IfThenDepedency;
import es.us.isa.idlreasoner.model.Model;
import es.us.isa.idlreasoner.model.OnlyOne;
import es.us.isa.idlreasoner.model.Or;
import es.us.isa.idlreasoner.model.OrDepdencny;
import es.us.isa.idlreasoner.model.RelationalDependency;
import es.us.isa.idlreasoner.model.Term;
import es.us.isa.idlreasoner.model.ZeroOrOne;
import es.us.isa.idlreasoner.util.CommonResources;
import es.us.isa.models.basics.BinaryLogicalOperator;
import es.us.isa.models.basics.BinaryLogicalPredicate;
import es.us.isa.models.basics.BinaryRelationalOperator;
import es.us.isa.models.basics.BinaryRelationalPredicate;
import es.us.isa.models.basics.Constant;
import es.us.isa.models.basics.NAryFunction;
import es.us.isa.models.basics.NumberConstant;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class CALETAMapper {
	
	private CommonResources cr;
	private String apiSpecificationPath;
	private String operationPath;
	private String operationType;
	
	private Model model;
	private Map<String, es.us.isa.IDL4OAS.Parameter<?>> mapParameter;
	Collection<NAryFunction<Boolean>> dependencies  = new ArrayList<NAryFunction<Boolean>>();
	
	public CALETAMapper(CommonResources cr, String apiSpecificationPath, String operationPath, String operationType) {
		this.cr = cr;
		this.apiSpecificationPath = apiSpecificationPath;
		this.operationPath = operationPath;
		this.operationType = operationType;
		
		mapJSON2IDL();
		mapVariables();
	}
	
	public void mapModel2CALETA() {
		List<Term> terms = model.getTerms();
		for (Term term : terms) {
			dependencies.add(processTerm(term));
		}
	}
	
	public Map<String, es.us.isa.IDL4OAS.Parameter<?>> getMapParameter(){
		return mapParameter;
	}
	
	public Collection<NAryFunction<Boolean>> getDependencies(){
		return dependencies;
	}
	
	// ---- General method ----
	
	private NAryFunction<Boolean> processTerm(Term term){
		return processTerm(term, false);
	}
	
	//TODO: Controlar los complex y los negados
	private NAryFunction<Boolean> processTerm(Term term, Boolean complex){
		NAryFunction<Boolean> p = null;
		
		Boolean negated = false;
		if(term.getNegated()!=null) {
			negated = term.getNegated();
		}
		
		if(term.getParameter()!=null) {
			//PresenceParameter
			if(term.getRelation()!=null) {
				p = processPresenceParameter(term);
			}else {
				//BinaryRelationPredicate
				if(StringUtils.isNumeric(term.getValue())) {
					p = processBinaryRelationPredicate(term);
				//RelationalObjectDependency
				}else {
					p = processRelationalObjectDependency(term);
				}
			}
		//And || Or
		}else if(term.getAnd()!=null || term.getOr()!=null) {
			p = processAndOr(term, false);
		// --- Dependencies ---
		// IfThen Dependency
		}else if(term.getIfTheDepedency()!=null) {
			p = processIfThenDependency(term.getIfTheDepedency(), complex);
		//OnlyOne Dependency
		}else if(term.getOnlyOne()!=null) {
			p = processOnlyOneDependency(term.getOnlyOne(), complex, negated);
		//Or Dependency
		}else if(term.getOrDepdencny()!=null) {
			p = processOrDependency(term.getOrDepdencny(), complex, negated);
		//AlOrNone Dependency
		}else if(term.getAllOrNone()!=null) {
			p = processAllOrNoneDependency(term.getAllOrNone(), complex, negated);
		//ZeroOrOne
		}else if(term.getZeroOrOne()!=null) {
			p = processZeroOrOneDependency(term.getZeroOrOne(), complex, negated);
		//RelationalDependency
		}else if(term.getRelationalDependency()!=null) {
			p = processRelationalDependency(term.getRelationalDependency());
		}
		
		return p;
	}
	
	// ---- IfThen Dependency ----
	
	@SuppressWarnings("unchecked")
	private IfThenDependency processIfThenDependency(IfThenDepedency dependency, Boolean complex) {
		IfThenDependency ifThenDependency;
		
		List<Term> terms = dependency.getTerms();
		List<NAryFunction<Boolean>> ifThenTerms = new ArrayList<>();
		for (Term term : terms) {
			NAryFunction<Boolean> p = processTerm(term);
			ifThenTerms.add(p);
		}
		
		ifThenDependency = new IfThenDependency(complex, ifThenTerms.get(0), ifThenTerms.get(1));
		
		return ifThenDependency;
		
	}
	
	// ---- OnlyOne Dependency ----
	
	private OnlyOneDependency processOnlyOneDependency(OnlyOne dependency, Boolean complex, Boolean negated) {
		OnlyOneDependency onlyOneDependency = null;
		List<Term> terms = dependency.getTerms();
		
		for (int i = 0; i < terms.size(); i++) {
			if(i==0) {
				onlyOneDependency = new OnlyOneDependency(negated ,complex ,processTerm(terms.get(i)));
			}else {
				onlyOneDependency.addTerm(processTerm(terms.get(i)));
			}
		}

		return onlyOneDependency;
		
	}
	
	// ---- Or Dependency ----
	
	private OrDependency processOrDependency(OrDepdencny dependency, Boolean complex, Boolean negated){
		OrDependency orDependency = null;
		List<Term> terms = dependency.getTerms();
		
		for (int i = 0; i < terms.size(); i++) {
			if(i==0) {
				orDependency = new OrDependency(negated, complex ,processTerm(terms.get(i)));
			}else {
				orDependency.addTerm(processTerm(terms.get(i)));
			}
		}
		
		return orDependency;
	}
	
	// ---- AllOrNone Dependency ----
	
	private AllOrNoneDependency processAllOrNoneDependency(AllOrNone dependency, Boolean complex, Boolean negated) {
		AllOrNoneDependency allOrNoneDependency = null;
		List<Term> terms = dependency.getTerms();
		
		for (int i = 0; i < terms.size(); i++) {
			if(i==0) {
				allOrNoneDependency = new AllOrNoneDependency(negated, complex, processTerm(terms.get(i)));
			}else {
				allOrNoneDependency.addTerm(processTerm(terms.get(i)));
			}
		}
		
		return allOrNoneDependency;
		
	}
	
	// ---- ZeroOrOne Dependency ----
	
	private ZeroOrOneDependency processZeroOrOneDependency(ZeroOrOne dependency, Boolean complex, Boolean negated) {
		ZeroOrOneDependency zeroOrOneDependency = null;
		List<Term> terms = dependency.getTerms();
		
		for (int i = 0; i < terms.size(); i++) {
			if(i==0) {
				zeroOrOneDependency = new ZeroOrOneDependency(negated, complex, processTerm(terms.get(i)));
			}else {
				zeroOrOneDependency.addTerm(processTerm(terms.get(i)));
			}
		}
		
		return zeroOrOneDependency;
	}
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Boolean> processRelationalDependency(RelationalDependency dependency){
		BinaryRelationalPredicate binaryRelationalPredicate = null;

		NAryFunction<Number> firstTerm = (NAryFunction<Number>) mapParameter.get(dependency.getFirstTerm());
		NAryFunction<Number> secondTerm = (NAryFunction<Number>) mapParameter.get(dependency.getSecondTerm());
		BinaryRelationalOperator operation =  mapOperator(dependency.getRelation());
		
		binaryRelationalPredicate = new BinaryRelationalPredicate(firstTerm, operation, secondTerm);
		return binaryRelationalPredicate;
	}
	
	
	// ---- Process And Or ----
	
	private NAryFunction<Boolean> processAndOr(Object item, Boolean complex){
		BinaryLogicalOperator operator = null;
		List<Term> termsJSON = new ArrayList<>();
		
		if(item instanceof And) {
			And and = (And) item;
			termsJSON = and.getTerms();
			operator = BinaryLogicalOperator.AND;
		}else if(item instanceof Or) {
			Or or = (Or) item;
			termsJSON = or.getTerms();
			operator = BinaryLogicalOperator.OR;
		}
		
		BinaryLogicalPredicate acum = null;
		
		for (int i = 1; i < termsJSON.size()-1; i++) {
			NAryFunction<Boolean> p2 = processTerm(termsJSON.get(i+1));
			if(i==0) {
				NAryFunction<Boolean> p1 = processTerm(termsJSON.get(i));
				acum = new BinaryLogicalPredicate(complex ,p1, operator, p2);
			}else {
				acum = new BinaryLogicalPredicate(complex, acum, operator, p2);
			}
		}
		
		return acum;		
	}
	
	// ---- Process Presence Parameter ----
	
	private NAryFunction<Boolean> processPresenceParameter(Term term){
		return new PresenceParameter(term.getParameter(), mapParameter.get(term.getParameter()), term.getPresence());
	}
	
	// ---- Process RelationalObject Dependency ----
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Boolean> processRelationalObjectDependency(Term term){
		NAryFunction<Boolean> p = null;
		
		if(term.getValue()=="true" || term.getValue()=="false") {
			Constant<Boolean> c = new Constant<Boolean>(new Boolean(term.getValue()));
			p = new RelationalObjectDependency<Boolean>((es.us.isa.IDL4OAS.Parameter<Boolean>) mapParameter.get(term.getParameter()), c);
		}
		return p;
	}
	
	// ---- Process BinaryRelation Predicate ----
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Boolean> processBinaryRelationPredicate(Term term){
		NAryFunction<Boolean> p;
		
		BinaryRelationalOperator relation = mapOperator(term.getRelation());
		
		NumberConstant number = new NumberConstant(new Integer(term.getValue()));
		p = new BinaryRelationalPredicate((es.us.isa.IDL4OAS.Parameter<Number>) mapParameter.get(term.getParameter()), relation, number);
		
		return p;
	}
	
	private BinaryRelationalOperator mapOperator(String operation) {
		switch(operation) {
			case "==":
				return BinaryRelationalOperator.EQUAL;
			case ">":
				return BinaryRelationalOperator.GREATER;
			case "<":
				return BinaryRelationalOperator.LESS;
			case ">=":
				return BinaryRelationalOperator.GREATEREQ;
			case "<=":
				return BinaryRelationalOperator.LESSEQ;
			case "!=":
				return BinaryRelationalOperator.DIFFERENT;
			default:
				return null;
		}
	}
	
	// ---- Map variables from OAS ----
	
	private void mapVariables() {
		OpenAPI openAPISpec = new OpenAPIV3Parser().read(apiSpecificationPath);
        Operation operation = getOasOperation(openAPISpec, operationPath, operationType);
        List<Parameter> parameters = operation.getParameters(); // NullPointerException would be thrown on purpose, to stop program
        if (operation.getRequestBody() != null) {
            if (parameters == null)
                parameters = new ArrayList<>();
            parameters.addAll(getFormDataParameters(operation));
        }
        for (Parameter parameter : parameters) {
        	//TODO
        	//Creo que este es el tipo de objeto
        	parameter.getSchema().getName();
        	es.us.isa.IDL4OAS.Parameter<?> p = new es.us.isa.IDL4OAS.Parameter<>(parameter.getName(), parameter.getRequired(), parameter.getDeprecated(), parameter.getAllowEmptyValue());
        	mapParameter.put(parameter.getName(), p);
		}
		
	}
	
	// ---- Map JSON to IDL model ----
	
	private void mapJSON2IDL() {
		initJosnFile(cr);
		IDL2JSONMapper idl = new IDL2JSONMapper(cr, "./" + IDL_FILES_FOLDER + "/" + "combinatorial8.idl");
		idl.mapIDL2JSON();
		ObjectMapper mapper = new ObjectMapper();
		try {
			model =  mapper.readValue(new File(cr.IDL_JSON_FILE), Model.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
