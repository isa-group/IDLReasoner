package es.us.isa.idlreasoner.mapper;

import io.swagger.v3.parser.OpenAPIV3Parser;
import org.eclipse.emf.common.util.URI;

import java.io.IOException;

public class IDLMapperCreator {

//    public IDLMapper(String idl, String operation, String oasLink,String operationType, String fileRoute) {
//        this.openAPI = new OpenAPIV3Parser().read(oasLink);
//        if(operationType.contains("get"))
//            this.parameters = openAPI.getPaths().get("/"+operation).getGet().getParameters();
//        if(operationType.contains("delete"))
//            this.parameters = openAPI.getPaths().get("/"+operation).getDelete().getParameters();
//        if(operationType.contains("post"))
//            this.parameters = openAPI.getPaths().get("/"+operation).getPost().getParameters();
//        if(operationType.contains("put"))
//            this.parameters = openAPI.getPaths().get("/"+operation).getPut().getParameters();
//
//
//        this.resource = resourceSet.getResource(URI.createFileURI("./"+ fileRoute+ "/"+idl), true);
//        ex.doGenerate(resource, null, null);
//
//        try {
//            this.generateFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}
