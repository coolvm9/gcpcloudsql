package me.workshop.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

// Implement GCP Secret later
public class LocalPropertiesLoader {


    private static Configuration configInstance;

    private static LocalPropertiesLoader instance;
    private Configuration config;

    private LocalPropertiesLoader(String propertiesFilePath) {
        this.config = loadProperties(propertiesFilePath);
    }

    public static LocalPropertiesLoader getInstance(String propertiesFilePath) {
        if (instance == null) {
            instance = new LocalPropertiesLoader(propertiesFilePath);
        }
        return instance;
    }

    public String getProperty(String propertyName) {
        return config.getString(propertyName);
    }

    private static Configuration getConfigInstance(String filePath) {
        if (configInstance == null) {
            configInstance = loadProperties(filePath);
        }
        return configInstance;
    }

    private static Configuration loadProperties(String filePath) {
        try {
            Configurations configs = new Configurations();
            FileBasedConfigurationBuilder<?> builder = configs.propertiesBuilder(filePath);
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
