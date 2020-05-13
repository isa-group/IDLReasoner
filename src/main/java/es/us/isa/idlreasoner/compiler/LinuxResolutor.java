package es.us.isa.idlreasoner.compiler;

import es.us.isa.idlreasoner.util.CommonResources;

public class LinuxResolutor extends Resolutor {

    public LinuxResolutor(CommonResources cr) {
        super(cr);
        minizincExe = "./minizinc/linux/bin/minizinc";
        commandProcessArgs[0] = "/bin/bash";
        commandProcessArgs[1] = "-c";
    }

}