package es.us.isa.idlreasoner.mapper;

import com.google.inject.Injector;
import es.us.isa.idlreasoner.util.CommonResources;
import es.us.isa.idl.IDLStandaloneSetupGenerated;
import es.us.isa.idl.generator.IDLGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import static es.us.isa.idlreasoner.util.Utils.terminate;

public class DependenciesMapper {

    private String idlSpecificationPath;
    private CommonResources cr;

    public DependenciesMapper(CommonResources cr, String idlSpecificationPath) {
        this.cr = cr;
        this.idlSpecificationPath = idlSpecificationPath;
        mapConstraints();
    }

    public void mapConstraints() {
        IDLGenerator idlGenerator = new IDLGenerator();
        idlGenerator.setFolderPath(cr.BASE_CONSTRAINTS_FILE.substring(0, cr.BASE_CONSTRAINTS_FILE.lastIndexOf("/")));
        Injector injector = new IDLStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        Resource resource = resourceSet.getResource(URI.createFileURI(idlSpecificationPath), true);
        try {
            idlGenerator.doGenerate(resource, null, null);
        } catch (Exception e) {
            terminate("There was an error processing the IDL file. Check that it does not contain any errors.\n", e);
        }
    }
}
