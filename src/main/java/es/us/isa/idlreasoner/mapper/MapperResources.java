package es.us.isa.idlreasoner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static es.us.isa.idlreasoner.util.FileManager.appendContentToFile;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.PARAMETER_NAMES_MAPPING_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.STRING_INT_MAPPING_FILE;

public class MapperResources {

    Map<String, Map.Entry<String, Boolean>> operationParameters;
    BiMap<String, String> parameterNamesMapping;
    BiMap<String, Integer> stringIntMapping;
    Integer stringToIntCounter;

    public MapperResources() {
        operationParameters = new HashMap<>();
        parameterNamesMapping = HashBiMap.create();
        stringIntMapping = HashBiMap.create();
        stringToIntCounter = 0;
    }

//    private String parseParamName(String paramName) {
//
//    }
}
