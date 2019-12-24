package es.us.isa.idlreasoner.mapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

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
}
