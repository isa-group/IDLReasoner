package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.interparamdep.InterparameterDependenciesLanguageStandaloneSetupGenerated;
import es.us.isa.interparamdep.generator.InterparameterDependenciesLanguageGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;


import static es.us.isa.idlreasoner.util.FileManager.appendContentToFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class MiniZincConstraintMapper extends AbstractConstraintMapper {

    InterparameterDependenciesLanguageGenerator idlGenerator = new InterparameterDependenciesLanguageGenerator();
    Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    private Resource resource;

    public MiniZincConstraintMapper(String idlSpecificationPath) {
        this.idlSpecificationPath = idlSpecificationPath;
        mapConstraints();
    }

    public void mapConstraints() {
        this.resource = resourceSet.getResource(URI.createFileURI("./"+ IDL_FILES_FOLDER + "/" + idlSpecificationPath), true);
        idlGenerator.doGenerate(resource, null, null);
    }

    public void setParamToValue(String parameter, String value) {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "constraint " + parameter + " = " + value + ";\n");
    }

    public void finishConstraintsFile() {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "solve satisfy;\n");
    }
}
