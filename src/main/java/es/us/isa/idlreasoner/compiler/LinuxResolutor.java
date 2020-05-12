package es.us.isa.idlreasoner.compiler;

public class LinuxResolutor extends Resolutor {

    public LinuxResolutor() {
        super();
        minizincExe = "./minizinc/linux/bin/minizinc";
        commandProcessArgs[0] = "/bin/bash";
        commandProcessArgs[1] = "-c";
    }

}