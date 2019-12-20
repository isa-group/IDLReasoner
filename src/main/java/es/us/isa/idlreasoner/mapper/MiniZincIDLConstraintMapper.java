package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.interparamdep.InterparameterDependenciesLanguageStandaloneSetupGenerated;
import es.us.isa.interparamdep.generator.InterparameterDependenciesLanguageGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;


import static es.us.isa.idlreasoner.util.FileManager.appendContentToFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

import java.io.BufferedReader;
import java.io.IOException;

public class MiniZincIDLConstraintMapper extends AbstractConstraintMapper {

    InterparameterDependenciesLanguageGenerator idlGenerator = new InterparameterDependenciesLanguageGenerator();
    Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    private Resource resource;

    public MiniZincIDLConstraintMapper(String idlSpecificationPath) {
        this.idlSpecificationPath = idlSpecificationPath;
        mapConstraints();
    }

    public void mapConstraints() {
        this.resource = resourceSet.getResource(URI.createFileURI("./"+ IDL_FILES_FOLDER + "/" + idlSpecificationPath), true);
        try {
            idlGenerator.doGenerate(resource, null, null);
        } catch (Exception e) {
            System.err.println("There was an error processing the IDL file. Check that it does not contain any errors.\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void setParamToValue(String parameter, String value) {
    	String lineToWrite = "constraint " + parameter + " = " + value + ";\n";
        appendContentToFile(FULL_CONSTRAINTS_FILE, lineToWrite);
    }

    public void finishConstraintsFile() {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "solve satisfy;\n");
    }
}
