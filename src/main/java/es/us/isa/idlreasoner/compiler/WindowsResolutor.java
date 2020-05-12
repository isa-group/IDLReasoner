package es.us.isa.idlreasoner.compiler;

import es.us.isa.idlreasoner.util.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static es.us.isa.idlreasoner.util.IDLConfiguration.*;

public class WindowsResolutor extends Resolutor {

	public WindowsResolutor() {
		super();
		minizincExe = "\"minizinc/windows/minizinc.exe\"";
		commandProcessArgs[0] = "cmd.exe";
		commandProcessArgs[1] = "/c";
	}

}

