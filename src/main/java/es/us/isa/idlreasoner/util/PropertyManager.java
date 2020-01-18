package es.us.isa.idlreasoner.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static es.us.isa.idlreasoner.util.IDLConfiguration.IDL_AUX_FOLDER;

/**
 *
 * @author Sergio Segura
 */
public class PropertyManager {

    private static Properties properties = null;
    private static String configPath = IDL_AUX_FOLDER + "/config.properties";
    private static WebContentAuxiliar webContent = new WebContentAuxiliar();

    public static String readProperty(String name) {

        if (properties==null) {
            properties = new Properties();
            try {
            	configPath = webContent.getPathAndCheck(configPath);
                properties.load(new FileInputStream(configPath));
            } catch (IOException e) {
                System.err.println("Error reading property file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return properties.getProperty(name);

    }
}