package es.us.isa.idlreasoner.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import es.us.isa.models.basics.NAryFunction;
import io.swagger.v3.oas.models.parameters.Parameter;

public interface CALETAVariableMapper {
	
	public List<Parameter> getParameters();
	public Map<String, es.us.isa.IDL4OAS.Parameter<?>> getMapParameter();
	public Map<String, Boolean> getMapRequiredParameter();
	public Collection<NAryFunction<Boolean>> getDependencies();

}
