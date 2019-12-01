package es.us.isa.idlreasoner.mapper;

import java.io.File;
import java.util.List;

import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public abstract class AbstractMapper {

    List<String> reservedWords;
    String constraintsFilePath = "./" + readProperty("aux_files_folder") + "/" + readProperty("constraints_file");
    File constraintsFile;

    AbstractMapper() {
        constraintsFile = recreateConstraintsFile();
    }

    File recreateConstraintsFile() {
        File file = new File(constraintsFilePath);
        file.delete();
        file.getParentFile().mkdirs();
        return file;
    }

    String changeIfReservedWord(String word) {
        if(reservedWords.contains(word)) {
            word = word + "_R";
        }
        return word;

    }
}
