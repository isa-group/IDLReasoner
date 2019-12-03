package es.us.isa.idlreasoner.mapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static es.us.isa.idlreasoner.util.PropertyManager.readProperty;

public abstract class AbstractMapper {

    List<String> reservedWords;

//    AbstractMapper() {
//        constraintsFile = recreateConstraintsFile();
//    }

    String changeIfReservedWord(String word) {
        if(reservedWords.contains(word)) {
            word = word + "_R";
        }
        return word;

    }
}
