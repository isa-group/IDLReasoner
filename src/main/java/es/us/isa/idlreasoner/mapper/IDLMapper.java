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

public class IDLMapper implements IDLMapperInterface {
	

	private Set<String> parametersParsed = new HashSet<>();
	private OpenAPI openAPI;
	private List<Parameter> parameters;
	private Map<String, Map<String, String>> mappingParameters = new HashMap<String, Map<String,String>>();
	
	private File f1 = createFileWithDirectory("./files", "minizinc.mzn");


	private List<String> reservedWords = Arrays.asList("annotation","any", "array", "bool", "case", "diff", 
			"div", "else", "elseif", "endif", "enum", "false", "float", "function", "if", "include",
			"intersect", "let", "list", "maximize", "minimize", "mod",  "of", "opt", "output", "par",
			"predicate", "record", "satisfy", "set", "solve", "string", "subset", "superset", "symdiff", "test",
			"then", "tuple", "type","union", "var", "where", "xor");

	
	
	InterparameterDependenciesLanguageGenerator ex = new InterparameterDependenciesLanguageGenerator();
	Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
	XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
	private Resource resource;
   
		
	public IDLMapper(String idl, String operation, String oasLink,String operationType, String fileRoute) {
		this.openAPI = new OpenAPIV3Parser().read(oasLink);
		if(operationType.contains("get"))
		this.parameters = openAPI.getPaths().get("/"+operation).getGet().getParameters();
		if(operationType.contains("delete"))
		this.parameters = openAPI.getPaths().get("/"+operation).getDelete().getParameters();
		if(operationType.contains("post"))
		this.parameters = openAPI.getPaths().get("/"+operation).getPost().getParameters();
		if(operationType.contains("put"))
		this.parameters = openAPI.getPaths().get("/"+operation).getPut().getParameters();


		this.resource = resourceSet.getResource(URI.createFileURI("./"+ fileRoute+ "/"+idl), true);
		ex.doGenerate(resource, null, null);
		
		try {
			this.generateFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private File createFileWithDirectory(String dirName, String fileName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dirName + "/" + fileName);

		return file;
	}


	public void generateFile() throws IOException {
		
	   	
	   	List<String> lines = new ArrayList<>();
        
        FileReader fr = new FileReader(f1);
        BufferedReader reader = new BufferedReader(fr);
		
        String line = reader.readLine();
        while(line!=null) {
        	String l = line;
        	List<String> reservedWordUsed = reservedWords.stream().filter(r->l.contains(r)).collect(Collectors.toList());
        	for(String r: reservedWordUsed) {
        		line = line.replace(r, reservedWords(r));
        	}
        	lines.add(line);
        	line = reader.readLine();
        }
        
        FileWriter fw = new FileWriter(f1);
        BufferedWriter out = new BufferedWriter(fw);
        
        String var;
        String varSet;
	   	for (Parameter parameter : parameters) {
	   		Schema<?> schema = parameter.getSchema();

	   		
			if(schema.getType()=="boolean") {
				var = "var bool: ";
			}else if(schema.getEnum()!=null) {
				Integer number = schema.getEnum().size()-1;
				
				if(number!=0) {
					var ="var 0.."+number+": ";
					Map<String,String> values = new HashMap<String, String>();
					
					for (int i = 0; i < schema.getEnum().size(); i++) {
						values.put(schema.getEnum().get(i).toString(),Integer.toString(i) );
					}
					mappingParameters.put(parameter.getName(), values);
				}
				
				else {
					var ="var 0..1: ";
				}
			}else {
			var ="var 0..1: ";
			}
			var +=reservedWords(parameter.getName())+";\n";
			if(!parametersParsed.contains(var)) {
				parametersParsed.add(var);
				out.append(var);
				}
			varSet = "var 0..1: " + reservedWords(parameter.getName())+"Set;\n";
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

	public String reservedWords(String parameter) {
		if(reservedWords.contains(parameter)) {
			parameter = parameter + parameter.charAt(parameter.length()-1);
		}
		return parameter;
	
	}
	
	public Map<String, Map<String,String>> getMappingParameters(){
		return this.mappingParameters;
	}
	
	public void setRestriction(String parameter, boolean put) {
		
		FileWriter fw;
		try {
			fw = new FileWriter(f1, true);
		    BufferedWriter out = new BufferedWriter(fw);
		    
		    if(put) {

		    out.append("constraint " + reservedWords(parameter)+"Set"+ " == 1;");
		    }else {

		      out.append("constraint " + reservedWords(parameter)+"Set"+ " == 0;");	
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
			fw = new FileWriter(f1, true);
		    BufferedWriter out = new BufferedWriter(fw);

		    out.append("constraint " + reservedWords(parameter)+ " == "+value+";");

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
			fw = new FileWriter(f1, true);
		    BufferedWriter out = new BufferedWriter(fw);
		 	out.append("solve satisfy;");
		    out.flush();	
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setFile(String directory ,String file) {
		this.f1 = new File("./"+directory+"/"+file+".mzn");
	}
	
	
		



}
