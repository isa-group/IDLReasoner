package es.us.isa.idlreasoner.mapper;

import static es.us.isa.idlreasoner.util.Utils.terminate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import es.us.isa.idl.IDLStandaloneSetupGenerated;
import es.us.isa.idl.generator.IDL2JSONGenerator;
import es.us.isa.idlreasoner.util.CommonResources;

public class IDL2JSONMapper {
	
    private String idlSpecificationPath;
    private CommonResources cr;
	
    public IDL2JSONMapper(CommonResources cr, String idlSpecificationPath) {
        this.cr = cr;
        this.idlSpecificationPath = idlSpecificationPath;
    }
	
	public void mapIDL2JSON() {	
		IDL2JSONGenerator jsonGenerator = new IDL2JSONGenerator();
		jsonGenerator.setFolderPath(cr.BASE_CONSTRAINTS_FILE.substring(0, cr.BASE_CONSTRAINTS_FILE.lastIndexOf("/")));
		Injector injector = new IDLStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        Resource resource = resourceSet.getResource(URI.createFileURI(idlSpecificationPath), true);
        try {
        	jsonGenerator.doGenerate(resource, null, null);
        } catch (Exception e) {
            terminate("There was an error processing the IDL file. Check that it does not contain any errors.\n", e);
        }
	}

}
