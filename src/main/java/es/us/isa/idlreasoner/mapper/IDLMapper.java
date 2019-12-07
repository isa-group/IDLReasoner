package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.interparamdep.InterparameterDependenciesLanguageStandaloneSetupGenerated;
import es.us.isa.interparamdep.generator.InterparameterDependenciesLanguageGenerator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static es.us.isa.idlreasoner.util.FileManager.createFileIfNotExists;
import static es.us.isa.idlreasoner.util.IDLConfiguration.BASE_CONSTRAINTS_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;

public class IDLMapper extends AbstractMapper {
	

	private Set<String> parametersParsed = new HashSet<>();
	private OpenAPI openAPI;
	private List<Parameter> parameters;
	private Map<String, Map<String, String>> mappingParameters = new HashMap<String, Map<String,String>>();
	
	InterparameterDependenciesLanguageGenerator ex = new InterparameterDependenciesLanguageGenerator();
	Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
	XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
	private Resource resource;
   
		
	public IDLMapper(String idl, String operation, String oasLink,String operationType) {
		reservedWords = Arrays.asList("annotation","any", "array", "bool", "case", "diff",
				"div", "else", "elseif", "endif", "enum", "false", "float", "function", "if", "include",
				"intersect", "let", "list", "maximize", "minimize", "mod",  "of", "opt", "output", "par",
				"predicate", "record", "satisfy", "set", "solve", "string", "subset", "superset", "symdiff", "test",
				"then", "tuple", "type","union", "var", "where", "xor");

		this.openAPI = new OpenAPIV3Parser().read(oasLink);
		if(operationType.contains("get"))
			this.parameters = openAPI.getPaths().get("/"+operation).getGet().getParameters();
		if(operationType.contains("delete"))
			this.parameters = openAPI.getPaths().get("/"+operation).getDelete().getParameters();
		if(operationType.contains("post"))
			this.parameters = openAPI.getPaths().get("/"+operation).getPost().getParameters();
		if(operationType.contains("put"))
			this.parameters = openAPI.getPaths().get("/"+operation).getPut().getParameters();


		this.resource = resourceSet.getResource(URI.createFileURI("./"+ IDL_FILES_FOLDER + "/" + idl), true);
		ex.doGenerate(resource, null, null);
		
		try {
			this.generateFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public void generateFile() throws IOException {
		
	   	
	   	List<String> lines = new ArrayList<>();

		File constraintsFile = createFileIfNotExists(BASE_CONSTRAINTS_FILE);
        FileReader fr = new FileReader(constraintsFile);
        BufferedReader reader = new BufferedReader(fr);
		
        String line = reader.readLine();
        while(line!=null) {
        	String l = line;
        	List<String> reservedWordUsed = reservedWords.stream().filter(r->l.contains(r)).collect(Collectors.toList());
        	for(String r: reservedWordUsed) {
        		line = line.replace(r, changeIfReservedWord(r));
        	}
        	lines.add(line);
        	line = reader.readLine();
        }
        
        FileWriter fw = new FileWriter(constraintsFile);
        BufferedWriter out = new BufferedWriter(fw);
        
        String var;
        String varSet;
	   	for (Parameter parameter : parameters) {
	   		Schema<?> schema = parameter.getSchema();

	   		
			if(schema.getType()=="boolean") {
				var = "var bool: ";
			} else if(schema.getEnum()!=null) {
				Integer number = schema.getEnum().size()-1;
				
				if(number!=0) {
					var ="var 0.."+number+": ";
					Map<String,String> values = new HashMap<String, String>();
					
					for (int i = 0; i < schema.getEnum().size(); i++) {
						values.put(schema.getEnum().get(i).toString(),Integer.toString(i) );
					}
					mappingParameters.put(parameter.getName(), values);
				} else {
					var ="var 0..1: ";
				}
			} else {
				var ="var 0..1: ";
			}
			var += changeIfReservedWord(parameter.getName())+";\n";
			if(!parametersParsed.contains(var)) {
				parametersParsed.add(var);
				out.append(var);
			}
			varSet = "var 0..1: " + changeIfReservedWord(parameter.getName())+"Set;\n";
			if(!parametersParsed.contains(varSet)) {
				parametersParsed.add(varSet);
				out.append(varSet);
			}
			
		}
	   	
	 	out.newLine();
	 	for (String fileLine : lines) {
	 		out.append(fileLine+"\n");
	 	}


	    out.flush();	
        out.close();
	}

	public List<String> getParameters(){
		List<String> res = new ArrayList<>();
		this.parameters.stream().forEach(p->res.add(p.getName()));

		return res;
	}
	
	public Map<String, Map<String,String>> getMappingParameters(){
		return this.mappingParameters;
	}
	
	public void setRestriction(String parameter, boolean put) {
		
		FileWriter fw;
		try {
			File constraintsFile = new File(BASE_CONSTRAINTS_FILE);
			fw = new FileWriter(constraintsFile, true);
		    BufferedWriter out = new BufferedWriter(fw);
		    
		    if(put) {
		    	out.append("constraint " + changeIfReservedWord(parameter)+"Set"+ " == 1;");
		    } else {
		    	out.append("constraint " + changeIfReservedWord(parameter)+"Set"+ " == 0;");
		    }
		 	out.newLine();
		    out.flush();	
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setValue(String parameter, String value){
		
		FileWriter fw;
		try {
			File constraintsFile = new File(BASE_CONSTRAINTS_FILE);
			fw = new FileWriter(constraintsFile, true);
		    BufferedWriter out = new BufferedWriter(fw);

		    out.append("constraint " + changeIfReservedWord(parameter)+ " == "+value+";");

		 	out.newLine();
		    out.flush();	
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	        
	}
	
	public void finishMinizincDocument() {
		
		FileWriter fw;
		try {
			File constraintsFile = new File(BASE_CONSTRAINTS_FILE);
			fw = new FileWriter(constraintsFile, true);
		    BufferedWriter out = new BufferedWriter(fw);
		 	out.append("solve satisfy;");
		    out.flush();	
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
