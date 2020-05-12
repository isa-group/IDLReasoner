package es.us.isa.idlreasoner.compiler;

import java.util.List;
import java.util.Map;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class ResolutorCreator {

	public static Resolutor createResolutor() {
		Resolutor resolutor = null;

		if (System.getProperty("os.name").toLowerCase().contains("windows"))
			resolutor = new WindowsResolutor();
		else if (System.getProperty("os.name").toLowerCase().contains("mac"))
			resolutor = new WindowsResolutor();
		else
			terminate("Operating system " + System.getProperty("os.name") + " not supported.");

		return resolutor;
	}

}
