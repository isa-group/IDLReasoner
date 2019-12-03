package es.us.isa.idlreasoner.mapper;

import java.io.IOException;

public abstract class AbstractVariableMapper extends AbstractMapper{

    private String apiSpecificationPath;

    abstract void mapVariables() throws IOException;
}
