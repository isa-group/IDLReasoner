package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.interparamdep.InterparameterDependenciesLanguageStandaloneSetupGenerated;
import es.us.isa.interparamdep.generator.InterparameterDependenciesLanguageGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

public class DependenciesMapper {

    private String idlSpecificationPath;

    public DependenciesMapper(String idlSpecificationPath) {
        this.idlSpecificationPath = idlSpecificationPath;
        mapConstraints();
    }

    public void mapConstraints() {
        InterparameterDependenciesLanguageGenerator idlGenerator = new InterparameterDependenciesLanguageGenerator();
        Injector injector = new InterparameterDependenciesLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        Resource resource = resourceSet.getResource(URI.createFileURI(idlSpecificationPath), true);
        try {
            idlGenerator.doGenerate(resource, null, null);
        } catch (Exception e) {
            System.err.println("There was an error processing the IDL file. Check that it does not contain any errors.\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
