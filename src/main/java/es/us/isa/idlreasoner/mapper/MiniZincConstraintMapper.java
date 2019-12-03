package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.interparamdep.InterparameterDependenciesLanguageStandaloneSetupGenerated;
import es.us.isa.interparamdep.generator.InterparameterDependenciesLanguageGenerator;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.us.isa.idlreasoner.util.FileManager.appendLine;
import static es.us.isa.idlreasoner.util.IDLConfiguration.CONSTRAINTS_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_FILES_FOLDER;

public class MiniZincConstraintMapper extends AbstractConstraintMapper {

    InterparameterDependenciesLanguageGenerator idlGenerator = new InterparameterDependenciesLanguageGenerator();
    Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    private Resource resource;

    public MiniZincConstraintMapper(String idlSpecificationPath) {
        this.idlSpecificationPath = idlSpecificationPath;
    }

    public void mapConstraints() {
        this.resource = resourceSet.getResource(URI.createFileURI("./"+ IDL_FILES_FOLDER + "/" + idlSpecificationPath), true);
        idlGenerator.doGenerate(resource, null, null);
    }

    public void setParamToValue(String parameter, String value) {
        appendLine(CONSTRAINTS_FILE, "constraint " + parameter + " = " + value + ";");
    }

    public void finishConstraintsFile() {
        appendLine(CONSTRAINTS_FILE, "solve satisfy;");
    }
}
