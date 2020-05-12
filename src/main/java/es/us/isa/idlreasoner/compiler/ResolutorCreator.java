package es.us.isa.idlreasoner.compiler;

import org.apache.commons.lang3.SystemUtils;

import java.util.List;
import java.util.Map;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class ResolutorCreator {

	public static Resolutor createResolutor() {
		Resolutor resolutor = null;

		if (SystemUtils.IS_OS_WINDOWS)
			resolutor = new WindowsResolutor();
		else if (SystemUtils.IS_OS_MAC)
			resolutor = new MacResolutor();
		else
			terminate("Operating system " + System.getProperty("os.name") + " not supported.");

		return resolutor;
	}

}