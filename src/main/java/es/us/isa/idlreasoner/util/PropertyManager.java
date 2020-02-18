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

    public static String readProperty(String name) {

        if (properties==null) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream("src/main/resources/idl-reasoner.properties"));
            } catch (IOException e) {
                System.err.println("Error reading property file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return properties.getProperty(name);

    }
}