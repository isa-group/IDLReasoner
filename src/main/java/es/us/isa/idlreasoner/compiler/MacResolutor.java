package es.us.isa.idlreasoner.compiler;

public class MacResolutor extends Resolutor {

    public MacResolutor() {
        super();
        minizincExe = "./minizinc/mac/minizinc";
        commandProcessArgs[0] = "/bin/bash";
        commandProcessArgs[1] = "-c";
    }

}