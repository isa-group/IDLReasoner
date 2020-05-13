package es.us.isa.idlreasoner.compiler;

import es.us.isa.idlreasoner.util.CommonResources;
import org.apache.commons.lang3.SystemUtils;

import java.util.List;
import java.util.Map;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;
import static es.us.isa.idlreasoner.util.Utils.terminate;

public class ResolutorCreator {

	public static Resolutor createResolutor(CommonResources cr) {
		Resolutor resolutor = null;

		if (SystemUtils.IS_OS_WINDOWS)
			resolutor = new WindowsResolutor(cr);
		else if (SystemUtils.IS_OS_MAC)
			resolutor = new MacResolutor(cr);
		else if (SystemUtils.IS_OS_LINUX)
			resolutor = new LinuxResolutor(cr);
		else
			terminate("Operating system " + System.getProperty("os.name") + " not supported.");

		return resolutor;
	}

}