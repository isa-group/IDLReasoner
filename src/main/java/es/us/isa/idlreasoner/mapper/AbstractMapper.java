package es.us.isa.idlreasoner.mapper;

import org.apache.commons.lang3.RandomStringUtils;

import static es.us.isa.interparamdep.generator.Utils.parseParamName;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
            if (paramFeatures.getKey().equals("string") || paramFeatures.getKey().equals("array")) {
                Integer intMapping = mr.stringIntMapping.get(value);
                if (intMapping != null) {
                    return Integer.toString(intMapping);
                } else {
                    int randomInt;
                    do { randomInt = ThreadLocalRandom.current().nextInt(1000, 9999); }
                    while (mr.stringIntMapping.inverse().get(randomInt)!=null);
                    mr.stringIntMapping.put(value, randomInt);
                    return Integer.toString(randomInt);
                }
            } else if (paramFeatures.getKey().equals("number")) {
                return value.replaceAll("\\.\\d+", "");
            }
        }

        return value;
    }

    String changedToOrigParamValue(String parameter, String value) {
        Map.Entry<String, Boolean> paramFeatures = mr.operationParameters.get(parameter);
        if (paramFeatures != null) {
            if (paramFeatures.getKey().equals("string") || paramFeatures.getKey().equals("array")) {
                String stringMapping;
                try {
                    stringMapping = mr.stringIntMapping.inverse().get(new Integer(value));
                } catch (NumberFormatException e) {
                    return value;
                }
                if (stringMapping != null) {
                    return stringMapping;
                } else {
                    String newStringMapping;
                    do { newStringMapping = RandomStringUtils.randomAscii(ThreadLocalRandom.current().nextInt(1,10)); }
                    while (mr.stringIntMapping.get(newStringMapping)!=null);
                    mr.stringIntMapping.put(newStringMapping, new Integer(value));
                    return newStringMapping;
                }
            }
        }

        return value;
    }
}
