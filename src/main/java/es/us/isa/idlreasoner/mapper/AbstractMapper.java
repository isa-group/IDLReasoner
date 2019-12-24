package es.us.isa.idlreasoner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBiMap;

import static es.us.isa.idlreasoner.util.Utils.parseParamName;
import static es.us.isa.interparamdep.generator.ReservedWords.RESERVED_WORDS;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static es.us.isa.idlreasoner.util.FileManager.appendContentToFile;
import static es.us.isa.idlreasoner.util.FileManager.recreateFile;
import static es.us.isa.idlreasoner.util.IDLConfiguration.PARAMETER_NAMES_MAPPING_FILE;
import static es.us.isa.idlreasoner.util.IDLConfiguration.STRING_INT_MAPPING_FILE;

public class AbstractMapper {

    String specificationPath;
    MapperResources mr;

    public AbstractMapper(MapperResources mr) {
        if (mr != null)
            this.mr = mr;
        else
            this.mr = new MapperResources();
    }

    String origToChangedParamName(String origParamName) {
        String changedParamName = mr.parameterNamesMapping.inverse().get(origParamName);
        if (changedParamName != null) {
            return changedParamName;
        } else {
            String parsedParamName = parseParamName(origParamName);
            if (!parsedParamName.equals(origParamName)) {
                mr.parameterNamesMapping.put(parsedParamName, origParamName);
                return parsedParamName;
            }
        }
        return origParamName;
    }

    String changedToOrigParamName(String changedParamName) {
        String origParamName = mr.parameterNamesMapping.get(changedParamName);
        if (origParamName != null) {
            return origParamName;
        }
        return changedParamName;
    }

    String origToChangedParamValue(String parameter, String value) {
        Map.Entry<String, Boolean> paramFeatures = mr.operationParameters.get(parameter);
        if (paramFeatures != null) {
            if (paramFeatures.getKey().equals("string")) {
                Integer intMapping = mr.stringIntMapping.get(value);
                if (intMapping != null) {
                    return Integer.toString(intMapping);
                } else {
                    int randomInt = ThreadLocalRandom.current().nextInt(1000, 9999);
                    mr.stringIntMapping.put(value, randomInt);
                    return Integer.toString(randomInt);
                }
            }
        }

        return value;
    }

    String changedToOrigParamValue(String parameter, String value) {
        Map.Entry<String, Boolean> paramFeatures = mr.operationParameters.get(parameter);
        if (paramFeatures != null) {
            if (paramFeatures.getKey().equals("string")) {
                String stringMapping;
                try {
                    stringMapping = mr.stringIntMapping.inverse().get(new Integer(value));
                } catch (NumberFormatException e) {
                    return value;
                }
                if (stringMapping != null) {
                    return stringMapping;
                } else {
                    return "default string";
                }
            }
        }

        return value;
    }
}
