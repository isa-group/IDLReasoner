package es.us.isa.idlreasoner.compiler;

import es.us.isa.idlreasoner.util.CommonResources;
import org.apache.commons.lang3.SystemUtils;

import java.util.List;
import java.util.Map;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class ResolutorCreator {

	public static Resolutor createResolutor(CommonResources cr) {
		return new Resolutor(cr);
	}

}