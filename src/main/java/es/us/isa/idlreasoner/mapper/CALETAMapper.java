package es.us.isa.idlreasoner.mapper;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;
import static es.us.isa.idlreasoner.util.IDLConfiguration.initJosnFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.isa.CSPModel.Variable;
import es.us.isa.IDL4OAS.PathItem;
import es.us.isa.IDL4OAS.PresenceParameter;
import es.us.isa.IDL4OAS.dependencies.AllOrNoneDependency;
import es.us.isa.IDL4OAS.dependencies.IfThenDependency;
import es.us.isa.IDL4OAS.dependencies.OnlyOneDependency;
import es.us.isa.IDL4OAS.dependencies.OrDependency;
import es.us.isa.IDL4OAS.dependencies.RelationalObjectDependency;
import es.us.isa.IDL4OAS.dependencies.ZeroOrOneDependency;
import es.us.isa.idlreasoner.model.AllOrNone;
import es.us.isa.idlreasoner.model.And;
import es.us.isa.idlreasoner.model.ArithmeticDependency;
import es.us.isa.idlreasoner.model.IfThenDepedency;
import es.us.isa.idlreasoner.model.Model;
import es.us.isa.idlreasoner.model.OnlyOne;
import es.us.isa.idlreasoner.model.Or;
import es.us.isa.idlreasoner.model.OrDepdencny;
import es.us.isa.idlreasoner.model.Parenthesis;
import es.us.isa.idlreasoner.model.RelationalDependency;
import es.us.isa.idlreasoner.model.Term;
import es.us.isa.idlreasoner.model.ZeroOrOne;
import es.us.isa.idlreasoner.util.CommonResources;
import es.us.isa.models.basics.ArithmeticBinaryFunction;
import es.us.isa.models.basics.BinaryArithmeticOperator;
import es.us.isa.models.basics.BinaryLogicalOperator;
import es.us.isa.models.basics.BinaryLogicalPredicate;
import es.us.isa.models.basics.BinaryRelationalOperator;
import es.us.isa.models.basics.BinaryRelationalPredicate;
import es.us.isa.models.basics.BooleanDomain;
import es.us.isa.models.basics.Constant;
import es.us.isa.models.basics.Domain;
import es.us.isa.models.basics.IntegerDomain;
import es.us.isa.models.basics.NAryFunction;
import es.us.isa.models.basics.NumberConstant;
import es.us.isa.models.basics.RealDomain;
import es.us.isa.models.basics.StringDomain;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class CALETAMapper extends AbstractMapper {
	
	private CommonResources cr;
	private String apiSpecificationPath;
	private String operationPath;
	private String operationType;
	private String idlFile;
	
	private Model model;
	private List<Parameter> parameters;
	private Map<String, es.us.isa.IDL4OAS.Parameter<?>> mapParameter;
	private Map<String, Boolean> mapRequiredParameter;
	private Map<String, PresenceParameter> mapPresence;
	private Collection<NAryFunction<Boolean>> dependencies  = new ArrayList<NAryFunction<Boolean>>();
	private Collection<NAryFunction<Boolean>> originalDependencies  = new ArrayList<NAryFunction<Boolean>>();
	private List<Parenthesis> processedOperators;
	private List<es.us.isa.idlreasoner.model.Operation> processedOperators2;
	
	
	public CALETAMapper(CommonResources cr, String idlFile, String apiSpecificationPath, String operationPath, String operationType) {
        super(cr);
		this.cr = cr;
		this.apiSpecificationPath = apiSpecificationPath;
		this.operationPath = operationPath;
		this.operationType = operationType;
		this.idlFile = idlFile;
		this.mapParameter = new HashMap<>();
		this.mapPresence = new HashMap<>();
		this.mapRequiredParameter = new HashMap<>();
		mapVariables();
		mapJSON2IDL();
		mapModel2CALETA();
		this.originalDependencies = new ArrayList<>(dependencies);
		completePresenceParameter();
	}
	
	
	public void mapModel2CALETA() {
		if(model!=null) {
			List<Term> terms = model.getTerms();
			for (Term term : terms) {
				dependencies.add(processTerm(term));
			}
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

	private NAryFunction<Boolean> processTerm(Term term, Boolean complex){
		NAryFunction<Boolean> p = null;
		
		Boolean negated = false;
		if(term.getNegated()!=null) {
			negated = term.getNegated();
		}
		//TODO
		if(term.getParameter()!=null) {
			if(term.getRelation()!=null) {
				if(StringUtils.isNumeric(term.getValue())) {
					p = processBinaryRelationPredicate(term, complex);
				//RelationalObjectDependency
				}else{
					p = processRelationalObjectDependency(term, complex);
				}
				//PresenceParameter
			}else if(term.getPresence()!=null) {
				p = processPresenceParameter(term);
			}
		//And || Or
		}else if(term.getAnd()!=null || term.getOr()!=null) {
			p = processAndOr(term, complex);
		// --- Dependencies ---
		// IfThen Dependency
		}else if(term.getIfThenDepedency()!=null) {
			p = processIfThenDependency(term.getIfThenDepedency(), complex);
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
			p = processRelationalDependency(term.getRelationalDependency(), complex);
		//ArithmeticDependency
		}else if(term.getArithmeticDependency()!=null) {
			p = processArithmeticDependency(term.getArithmeticDependency(), complex);
		}
		
		return p;
	}
	
	// ---- IfThen Dependency ----

	private NAryFunction<Boolean> processParameter(Term term) {
		return (NAryFunction<Boolean>) mapParameter.get(term.getParameter());
	}


	@SuppressWarnings("unchecked")
	private IfThenDependency processIfThenDependency(IfThenDepedency dependency, Boolean complex) {
		IfThenDependency ifThenDependency;
		
		List<Term> terms = dependency.getTerms();
		List<NAryFunction<Boolean>> ifThenTerms = new ArrayList<>();
		for (Term term : terms) {
			NAryFunction<Boolean> p = processTerm(term, true);
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
				onlyOneDependency = new OnlyOneDependency(negated ,complex ,processTerm(terms.get(i), true));
			}else {
				onlyOneDependency.addTerm(processTerm(terms.get(i), true));
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
				orDependency = new OrDependency(negated, complex ,processTerm(terms.get(i), true));
			}else {
				orDependency.addTerm(processTerm(terms.get(i), true));
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
				allOrNoneDependency = new AllOrNoneDependency(negated, complex, processTerm(terms.get(i), true));
			}else {
				allOrNoneDependency.addTerm(processTerm(terms.get(i), true));
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
				zeroOrOneDependency = new ZeroOrOneDependency(negated, complex, processTerm(terms.get(i), true));
			}else {
				zeroOrOneDependency.addTerm(processTerm(terms.get(i), true));
			}
		}
		
		return zeroOrOneDependency;
	}
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Boolean> processRelationalDependency(RelationalDependency dependency, Boolean complex){
		BinaryRelationalPredicate binaryRelationalPredicate = null;

		NAryFunction<Number> firstTerm = (NAryFunction<Number>) mapParameter.get(dependency.getFirstTerm());
		NAryFunction<Number> secondTerm = (NAryFunction<Number>) mapParameter.get(dependency.getSecondTerm());
		BinaryRelationalOperator operation =  mapOperator(dependency.getRelation());
		
		binaryRelationalPredicate = new BinaryRelationalPredicate(firstTerm, operation, secondTerm, complex);
		return binaryRelationalPredicate;
	}
	
	// ----- Process ArithmeticDependency -----
	
	
	private NAryFunction<Boolean> processArithmeticDependency(ArithmeticDependency arithmeticDependency,
			Boolean complex) {
		BinaryRelationalPredicate res = null;
		ArithmeticBinaryFunction acum = null;
		NAryFunction<Number> result = null;
		//NumberConstant c = new NumberConstant(new Double(arithmeticDependency.getResult()));
		String relation = arithmeticDependency.getRelation();
		String resultString = arithmeticDependency.getResult();
		
		processedOperators2 = new ArrayList<es.us.isa.idlreasoner.model.Operation>();
		
		List<es.us.isa.idlreasoner.model.Operation> elements = arithmeticDependency.getOperation();
		
		List<es.us.isa.idlreasoner.model.Operation> operators = elements.stream().
				filter(p -> p.getOperation()!=null).
				collect(Collectors.toList());
		
		List<es.us.isa.idlreasoner.model.Operation> priorityOperators = elements.stream().
				filter(p -> p.getOperation()!=null).
				filter(p->p.getOperation()=="*" || p.getOperation()=="/").
				collect(Collectors.toList());
		
		int i=0;
		for (es.us.isa.idlreasoner.model.Operation operator : operators) {
			int index = elements.indexOf(operator);
			if(!processedOperators2.contains(operator)) {	
				NAryFunction<Number> firstElement;
				NAryFunction<Number> secondElement = processOperationElement(elements.get(index+1));
				if(acum!=null) {
					firstElement = acum;
				}else {
					firstElement = processOperationElement(elements.get(index-1));
				}
				if(priorityOperators.contains(operator)) {
					acum = generateArithmeticBinaryFunction(firstElement, secondElement, operator.getOperation());
				}else {
					if(operators.size()>i+1 && priorityOperators.contains(operators.get(i+1))) {
						secondElement = processPriorityOperators(elements, operators, priorityOperators, operator);
					}else{
						acum = generateArithmeticBinaryFunction(firstElement, secondElement, operator.getOperation());
						}
					}
				}
				i++;
			}
		
		if(mapParameter.get(resultString)!=null) {
			result = (es.us.isa.IDL4OAS.Parameter<Number>) mapParameter.get(resultString);
		}else {
			result = new NumberConstant(new Double(resultString));
		}
		res = new BinaryRelationalPredicate(acum, translateRelation(relation), result);
		res.setComplex(complex);
		
		return res;
	}
	
	private BinaryRelationalOperator translateRelation(String relation) {
		switch(relation) {
			case ">":
				return BinaryRelationalOperator.GREATER;
			case ">=":
				return BinaryRelationalOperator.GREATEREQ;
			case "<":
				return BinaryRelationalOperator.LESS;
			case "<=":
				return BinaryRelationalOperator.LESSEQ;
			case "=":
				return BinaryRelationalOperator.EQUAL;
			case "<>":
				return BinaryRelationalOperator.DIFFERENT;
			default:
				return null;
		}
	}


	private ArithmeticBinaryFunction processParenthesis(List<Parenthesis> parenthesis) {
		ArithmeticBinaryFunction res = null;
			List<Parenthesis> elements = parenthesis;
			List<Parenthesis> operators = elements.stream().
					filter(p -> p.getOperation()!=null).
					collect(Collectors.toList());
			List<Parenthesis> priorityOperators = elements.stream().
					filter(p -> p.getOperation()!=null).
					filter(p->p.getOperation()=="*" || p.getOperation()=="/").
					collect(Collectors.toList());
			processedOperators = new ArrayList<>();
			int i=0;
			for (Parenthesis operator : operators) {
				if(!processedOperators.contains(operator)) {			
					Integer index = elements.indexOf(operator);
					NAryFunction<Number> secondElement = processParenthesisElement(elements.get(index+1));
					NAryFunction<Number> firstElement;
					if(res!=null) {
						firstElement = res;
					}else {
						firstElement = processParenthesisElement(elements.get(index-1));
					}
					if(priorityOperators.contains(operator)) {
						res = generateArithmeticBinaryFunction(firstElement, secondElement, operator.getOperation());
					}else {
						if(operators.size()>i+1 && priorityOperators.contains(operators.get(i+1))) {
							//TODO
							secondElement = processPriorityOperators(elements, operators, priorityOperators, operator);
						}else {
							res = generateArithmeticBinaryFunction(firstElement, secondElement, operator.getOperation());
						}
					}
					
					i++;
				}
			}
		
		return res;
	}
	
	private ArithmeticBinaryFunction processPriorityOperators(
			List<Parenthesis> elements,
			List<Parenthesis> operators,
			List<Parenthesis> priorityOperators,
			Parenthesis operator) {
		ArithmeticBinaryFunction res = null;
		NAryFunction<Number> firstElement;
		NAryFunction<Number> secondElement;
		while(true) {
			Integer index = elements.indexOf(operator);
			Integer operatorIndex = operators.indexOf(operator);
			if(res!=null) {
				firstElement = res;
			}else {
				firstElement = processParenthesisElement(elements.get(index-1));
			}
			secondElement = processParenthesisElement(elements.get(index+1));
			res = generateArithmeticBinaryFunction(firstElement,secondElement,operator.getOperation());
			processedOperators.add(operator);
			if(priorityOperators.contains(operators.get(operatorIndex+1))) {
				operator = operators.get(operatorIndex+1);
			}else {
				break;
			}
		}
		return res;
	}
	
	private ArithmeticBinaryFunction processPriorityOperators(
			List<es.us.isa.idlreasoner.model.Operation> elements,
			List<es.us.isa.idlreasoner.model.Operation> operators,
			List<es.us.isa.idlreasoner.model.Operation> priorityOperators,
			es.us.isa.idlreasoner.model.Operation operator) {
		
		ArithmeticBinaryFunction res = null;
		NAryFunction<Number> firstElement;
		NAryFunction<Number> secondElement;
		while(true) {
			Integer index = elements.indexOf(operator);
			Integer operatorIndex = operators.indexOf(operator);
			if(res!=null) {
				firstElement = res;
			}else {
				firstElement = processOperationElement(elements.get(index-1));
			}
			secondElement = processOperationElement(elements.get(index+1));
			res = generateArithmeticBinaryFunction(firstElement,secondElement,operator.getOperation());
			processedOperators2.add(operator);
			if(priorityOperators.contains(operators.get(operatorIndex+1))) {
				operator = operators.get(operatorIndex+1);
			}else {
				break;
			}
		}
		return res;
	}
	
	
	private ArithmeticBinaryFunction generateArithmeticBinaryFunction(NAryFunction<Number> firstElemen, NAryFunction<Number> secondElement, String operator) {
		ArithmeticBinaryFunction res = null;
		res = new ArithmeticBinaryFunction(firstElemen, translateOperator(operator), secondElement);
		return res;
	}
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Number> processParenthesisElement(Parenthesis parenthesis){
		 NAryFunction<Number> res = null;
		 if(parenthesis.getParameter()!=null) {
			 res = (es.us.isa.IDL4OAS.Parameter<Number>) mapParameter.get(parenthesis.getParameter());
		 }else {
			 res = processParenthesis(parenthesis.getParenthesis());
		 }
		 return res;
	}

	@SuppressWarnings("unchecked")
	private NAryFunction<Number> processOperationElement(es.us.isa.idlreasoner.model.Operation op){
		 NAryFunction<Number> res = null;
		 if(op.getParameter()!=null) {
			 res = (es.us.isa.IDL4OAS.Parameter<Number>) mapParameter.get(op.getParameter());
		 }else {
			 res = processParenthesis(op.getParenthesis());
		 }
		 return res;
	}
	
	private BinaryArithmeticOperator translateOperator(String op) {
		switch(op) {
			case "+":
				return BinaryArithmeticOperator.PLUS;
			case "-":
				return BinaryArithmeticOperator.MINUS;
			case "*":
				return BinaryArithmeticOperator.MULTIPLY;
			case "/":
				return BinaryArithmeticOperator.DIVISION;
			case "%":
				return BinaryArithmeticOperator.MOD;
			case "^":
				return BinaryArithmeticOperator.POW;
			default:
				return null;
		}
	}
	
	// ---- Process And Or ----
	
	private NAryFunction<Boolean> processAndOr(Term term, Boolean complex){
		BinaryLogicalOperator operator = null;
		List<Term> termsJSON = new ArrayList<>();
		Boolean negated = false;
		
		if(term.getAnd()!=null) {
			And and = term.getAnd();
			negated = and.getNegated();
			termsJSON = and.getTerms();
			operator = BinaryLogicalOperator.AND;
		}else if(term.getOr()!=null) {
			Or or = term.getOr();
			negated = or.getNegated();
			termsJSON = or.getTerms();
			operator = BinaryLogicalOperator.OR;
		}
		
		BinaryLogicalPredicate acum = null;
		
		if(termsJSON.size()>2) {
			for (int i = 0; i < termsJSON.size()-1; i++) {
				NAryFunction<Boolean> p2 = processTerm(termsJSON.get(i+1), true);
				if(i==0) {
					NAryFunction<Boolean> p1 = processTerm(termsJSON.get(i), true);
					acum = new BinaryLogicalPredicate(complex ,p1, operator, p2);
				}else {
					acum = new BinaryLogicalPredicate(complex, acum, operator, p2);
				}
			}
			
		}else {
			acum = new BinaryLogicalPredicate(complex ,processTerm(termsJSON.get(0), true), operator, processTerm(termsJSON.get(1), true));
			acum.setNegated(negated);
		}
		
		return acum;		
	}
	
	// ---- Process Presence Parameter ----
	
	private NAryFunction<Boolean> processPresenceParameter(Term term){
		Boolean presence = term.getPresence();
		if(term.getNegated()) {
			presence = !presence;
		}
		PresenceParameter p = new PresenceParameter(term.getParameter(), mapParameter.get(term.getParameter()), presence);
		mapPresence.put(mapParameter.get(term.getParameter()).getName(), p);
		return p;
	}
	
	// ---- Process RelationalObject Dependency ----
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Boolean> processRelationalObjectDependency(Term term, Boolean complex){
		NAryFunction<Boolean> p = null;
		es.us.isa.IDL4OAS.Parameter<?> parameter = mapParameter.get(term.getParameter());
		if(parameter.getDomain() instanceof BooleanDomain) {
			Constant<Boolean> c = new Constant<Boolean>(new Boolean(term.getValue()));
			RelationalObjectDependency<Boolean> r = new RelationalObjectDependency<Boolean>((es.us.isa.IDL4OAS.Parameter<Boolean>) parameter, c, Boolean.class);
			r.setComplex(true);
			p = r;	
		}else if(parameter.getDomain() instanceof StringDomain) {
			List<String> values = term.getValues();
			int i=0;
			for (String value : values) {
				Constant<String> c = new Constant<String>(value);
				RelationalObjectDependency<String> r = new RelationalObjectDependency<String>((es.us.isa.IDL4OAS.Parameter<String>) parameter, c, String.class);
				r.setComplex(true);
				if(i==0) {
					p = r;	
				}else {
					p = new BinaryLogicalPredicate(true, p, BinaryLogicalOperator.OR, r);
				
				}
				i++;
			}
			
		}else if(parameter.getDomain() instanceof IntegerDomain || parameter.getDomain() instanceof RealDomain) {
			//Constant<Integer> c = new Constant<Integer>(new Integer(term.getValue()));
			//RelationalObjectDependency<Integer> r = new RelationalObjectDependency<Integer>((es.us.isa.IDL4OAS.Parameter<Integer>) parameter, c, Integer.class);
			p = processBinaryRelationPredicate(term, true);
		}
		return p;
	}
	
	// ---- Process BinaryRelation Predicate ----
	
	@SuppressWarnings("unchecked")
	private NAryFunction<Boolean> processBinaryRelationPredicate(Term term, Boolean complex){
		NAryFunction<Boolean> p;
		
		BinaryRelationalOperator relation = mapOperator(term.getRelation());
		
		NumberConstant number = new NumberConstant(new Integer(term.getValue()));
		p = new BinaryRelationalPredicate((es.us.isa.IDL4OAS.Parameter<Number>) mapParameter.get(term.getParameter()), relation, number, complex);
		
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
	
	@SuppressWarnings("unchecked")
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
	
	// ---- Map JSON to IDL model ----
	
	private void mapJSON2IDL() {
		if(idlFile!=null) {
			initJosnFile(cr);
			IDL2JSONMapper idl = new IDL2JSONMapper(cr, "./" + IDL_FILES_FOLDER + "/" + idlFile);
			idl.mapIDL2JSON();
			ObjectMapper mapper = new ObjectMapper();
			try {
				model =  mapper.readValue(new File(cr.IDL_JSON_FILE), Model.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
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

    //Auxiliar methods
    
    public RelationalObjectDependency<Boolean> putPresenceParameter(String parameter, Boolean presence){
    	Constant<Boolean> c = new Constant<Boolean>(presence);
    	PresenceParameter p = mapPresence.get(parameter);
    	if(p==null) {
    		p = new PresenceParameter(mapParameter.get(parameter).getName(), mapParameter.get(parameter), true);
    		mapPresence.put(parameter, p);
    	}
    	//p1
    	RelationalObjectDependency<Boolean> res = new RelationalObjectDependency<Boolean>(p, c, Boolean.class);
    	
    	return res;
    }
    
    @SuppressWarnings("unchecked")
	public RelationalObjectDependency<?> putParameterValue(String parameter, String value) throws Exception{
    	es.us.isa.IDL4OAS.Parameter<?> p = mapParameter.get(parameter);
    	RelationalObjectDependency<?> res = null;
    	//Integer
    	Domain domain = p.getDomain();
    	if(domain instanceof BooleanDomain) {
    		if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) { 			
	    		Boolean boolValue = new Boolean(value);
	        	Constant<Boolean> c = new Constant<Boolean>(boolValue);
	        	es.us.isa.IDL4OAS.Parameter<Boolean> param = (es.us.isa.IDL4OAS.Parameter<Boolean>) p;
	        	res = new RelationalObjectDependency<Boolean>(param, c, Boolean.class);
    		}else {
    			throw new Exception("Can't convert string in Boolean");
    		}
        //String
    	}else if(domain instanceof IntegerDomain) {
    		Integer intValue = new Integer(value);
        	Constant<Integer> c = new Constant<Integer>(intValue);
        	es.us.isa.IDL4OAS.Parameter<Integer> param = (es.us.isa.IDL4OAS.Parameter<Integer>) p;
        	res = new RelationalObjectDependency<Integer>(param, c, Integer.class);
        //Boolean
    	}else if(domain instanceof StringDomain) {
    		value = value.replaceAll("^\"|\"$", "");
        	Constant<String> c = new Constant<String>(value);
        	es.us.isa.IDL4OAS.Parameter<String> param = (es.us.isa.IDL4OAS.Parameter<String>) p;
        	res = new RelationalObjectDependency<String>(param, c, String.class);
    	//Double
    	}else if(domain instanceof RealDomain) {
    		Double doubleValue = new Double(value);
        	Constant<Double> c = new Constant<Double>(doubleValue);
        	es.us.isa.IDL4OAS.Parameter<Double> param = (es.us.isa.IDL4OAS.Parameter<Double>) p;
        	res = new RelationalObjectDependency<Double>(param, c, Double.class);
    	}
    	
    	return res;
    }
    
    public Boolean isRequired(String paramName) {
    	return mapRequiredParameter.get(paramName);
    }
    
    public Set<String> getOperationParameters(){
    	Set<String> res = new TreeSet<String>();
    	if(mapParameter.keySet().size()>0) {
    		res = mapParameter.keySet();
    	}
    	return res;
    }
    
    public void resetCurrentProblem() {
    	System.out.println(dependencies);
    	System.out.println(originalDependencies);
    	this.dependencies = new ArrayList<>(originalDependencies);
    }
    
    private <T> SortedSet<T> transformList2SortedSet(List<T> values){
    	SortedSet<T> res = new TreeSet<T>();
		for (T value: values) {
			res.add(value);
		}
		return res;
    	
    }
    
    private void completePresenceParameter() {
    	if(parameters!=null) {
    		for (Parameter param : parameters) {
				if(!mapPresence.keySet().contains(param.getName())) {
					String paramName = param.getName();
				   	Constant<Boolean> cTrue = new Constant<Boolean>(true);
				   	Constant<Boolean> cFalse = new Constant<Boolean>(false);
		    		PresenceParameter p = new PresenceParameter(paramName, mapParameter.get(paramName), true);
		    		mapPresence.put(paramName, p);

			    	RelationalObjectDependency<Boolean> rel1 = new RelationalObjectDependency<Boolean>(p, cTrue, Boolean.class);
			    	rel1.setComplex(true);
			    	RelationalObjectDependency<Boolean> rel2 = new RelationalObjectDependency<Boolean>(p, cFalse, Boolean.class);
			    	rel2.setComplex(true);
			    	BinaryLogicalPredicate res = new BinaryLogicalPredicate(false ,rel1, BinaryLogicalOperator.OR, rel2);
			    	dependencies.add(res);
				}
			}
    	}
    }
    

	
}
