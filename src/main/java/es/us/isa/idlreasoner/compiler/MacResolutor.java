package es.us.isa.idlreasoner.compiler;

import es.us.isa.idlreasoner.util.CommonResources;

public class MacResolutor extends Resolutor {

    public MacResolutor(CommonResources cr) {
        super(cr);
        minizincExe = "./minizinc/mac/minizinc";
        commandProcessArgs[0] = "/bin/bash";
        commandProcessArgs[1] = "-c";
    }

}