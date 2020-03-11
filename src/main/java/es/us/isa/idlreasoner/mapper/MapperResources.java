package es.us.isa.idlreasoner.mapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import static com.google.common.collect.Maps.filterEntries;
import static com.google.common.collect.Maps.filterValues;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public void resetStringIntMapping() {
        stringIntMapping = HashBiMap.create(stringIntMapping.entrySet().stream()
                .filter(entry -> entry.getValue() < stringToIntCounter)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
