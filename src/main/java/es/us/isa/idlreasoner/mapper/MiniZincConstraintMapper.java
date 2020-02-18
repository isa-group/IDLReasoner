package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.interparamdep.InterparameterDependenciesLanguageStandaloneSetupGenerated;
import es.us.isa.interparamdep.generator.InterparameterDependenciesLanguageGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;


import static es.us.isa.idlreasoner.util.FileManager.appendContentToFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class MiniZincConstraintMapper extends AbstractMapper {

    InterparameterDependenciesLanguageGenerator idlGenerator = new InterparameterDependenciesLanguageGenerator();
    Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    private Resource resource;

    public MiniZincConstraintMapper(String idlSpecificationPath, MapperResources mr) {
        super(mr);
        this.specificationPath = idlSpecificationPath;
        mapConstraints();
    }

    public void mapConstraints() {
        this.resource = resourceSet.getResource(URI.createFileURI(specificationPath), true);
        try {
            idlGenerator.doGenerate(resource, null, null);
        } catch (Exception e) {
            System.err.println("There was an error processing the IDL file. Check that it does not contain any errors.\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void setParamToValue(String parameter, String value) {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "constraint " + origToChangedParamName(parameter) + " = " + origToChangedParamValue(parameter, value) + ";\n");
    }

    public void setParamToValue(String changedParamName, String origParamName, String value) {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "constraint " + origToChangedParamName(changedParamName) + " = " + origToChangedParamValue(origParamName, value) + ";\n");
    }

    public void finishConstraintsFile() {
        appendContentToFile(FULL_CONSTRAINTS_FILE, "solve satisfy;\n");
    }
}
