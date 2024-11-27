package it.gend.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Daniele Asteggiante
 */
public class PropertiesUtils {
    public static Properties properties;

    private PropertiesUtils(String propertiesFileName) {
        System.out.println("Loading properties file: " + propertiesFileName);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            System.err.println("Error during properties file loading " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        if (properties == null)
            new PropertiesUtils("application.properties");
        return properties.getProperty(key);
    }
}
