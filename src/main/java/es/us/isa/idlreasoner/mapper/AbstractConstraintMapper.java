package es.us.isa.idlreasoner.mapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractConstraintMapper extends AbstractMapper {

    String idlSpecificationPath;

    abstract public void mapConstraints();

    abstract public void setParamToValue(String parameter, String value);

    abstract public void finishConstraintsFile();
}
