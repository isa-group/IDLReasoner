package es.us.isa.idlreasoner.compiler;

import static es.us.isa.idlreasoner.util.Utils.terminate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.us.isa.CSPModel.CSPModel;
import es.us.isa.CSPModel.transformations.IDL4OASModelToCSPController;
import es.us.isa.IDL4OAS.IDL4OASModel;
import es.us.isa.IDL4OAS.Operation;
import es.us.isa.IDL4OAS.Parameter;
import es.us.isa.IDL4OAS.PathItem;
import es.us.isa.general.ModelObserver;
import es.us.isa.idlreasoner.util.IDLConfiguration;
import es.us.isa.models.basics.NAryFunction;
import es.us.isa.z3Model.transformations.CSPModelToZ3ModelController;

public class CALETAResolutor {
	
	private String operationPath;
	private String operationType;
	private CSPModel CSPmodel;
	private IDL4OASModel IDLModel;
	private Collection<Parameter<?>> parameters;
	private Collection<NAryFunction<Boolean>> dependencies;
	private Collection<NAryFunction<Boolean>> originalDependencies;
	private ModelObserver<CSPModel> controller;
	
	public CALETAResolutor(String apiSpecificationPath, String operationPath, String operationType, Collection<Parameter<?>> parameters, Collection<NAryFunction<Boolean>> dependencies){
		this.operationPath = operationPath;
		this.operationType = operationType;
		this.parameters = parameters;
		this.dependencies = dependencies;
		this.originalDependencies = dependencies;
		
	}
	
	public void addListener() {
		if(IDLConfiguration.SOLVER.toLowerCase().equals("z3")) {
			addZ3Listener();
		}else {
			terminate("Solver " + IDLConfiguration.SOLVER.toLowerCase() + " not supported for CALETA Analyzer");
		}
	}
	
	private void addZ3Listener() {
		instanceModels();
		controller = new CSPModelToZ3ModelController(new Integer(IDLConfiguration.MAX_RESULTS));
		IDL4OASModelToCSPController idl2CSPcontroller = new IDL4OASModelToCSPController(CSPmodel);
		IDLModel.registerObserver(idl2CSPcontroller);	
		CSPmodel.registerObserver(controller);
	}
	
	public void finishModel() {
		Map<String, Operation> operations = new HashMap<String, Operation>(); 
		Operation operation = new Operation(parameters, dependencies);
		operations.put(operationType, operation);
		PathItem path = new PathItem(operationPath, "NaN", "NaN", operations, parameters);
		IDLModel.addPathItem(path);
	}
	
	public void resetDependencies() {		
		List<NAryFunction<Boolean>> aux = new ArrayList<>(originalDependencies);
		addZ3Listener();
		dependencies = new ArrayList<>(aux);
	}
	
	public void addDependency(NAryFunction<Boolean> dependency) {	
		dependencies.add(dependency);

	}
	
	public Map<String, String> getASolution(){
		Map<String, String> res = filterSolution(controller.getASolution());
		return res;		
	}
	
	public Map<String, String> getRandomSolution(){
		Map<String, String> res = filterSolution(controller.getRandomSolution());
		return res;
	}
	
	public Map<String, String> getRandomNotValidSolution(){
		Map<String, String> res = filterSolution(controller.getRandomNotValidSolution());
		if(res.keySet().size()==0) {
			res = null;
		}
		return res;
	}
	
	private Map<String, String> filterSolution(Map<String, String> solution){
		Map<String, String> res = new HashMap<>();
		if(solution.keySet().size()==0) {
			return null;
		}
		Set<String> parameters = solution.keySet();
		for (String param : parameters) {
			Boolean isParam = this.parameters.stream().anyMatch(p->p.getName().trim().equals(param));
			if(isParam) {
				if(dependencies.size()!=0) {			
					if(parameters.contains(param+"Set")) {
						if(solution.get(param+"Set").equals("true")) {
							res.put(param, solution.get(param).replace("\"", ""));
						}
					}
				}else {
					res.put(param, solution.get(param).replace("\"", ""));
				}
			}
			
			//else if(solution.get(param).equals("true")) {
			//	res.put(param, solution.get(param));
			//}
		}
		return res;
	}
	
	public List<Map<String, String>> getAllSolutions(){
		Set<Map<String, String>> aux = new LinkedHashSet<>();
		List<Map<String, String>> res;
		List<Map<String, String>> solutions =  controller.getAllSolutions();
		for (Map<String, String> sol : solutions) {
			aux.add(filterSolution(sol));
		}
		res = new ArrayList<>(aux);
		if(res.get(0)==null) {
			res = new ArrayList<>();
		}
		if(dependencies.size()==0 || res.get(0)==null) {
			res.add(new HashMap<>());
		}
		return res;
	}
	
	public String solve() {
		return controller.solve();
	}

	private void instanceModels() {
		IDLModel = new IDL4OASModel();
		CSPmodel = new CSPModel();
	}
}
