package ru.sfedu.projectmanager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanager.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;


public class ConfigurationXmlUtil implements ConfigurationUtil {
    private static final Logger logger = LogManager.getLogger(ConfigurationPropertiesUtil.class);
    private static final Properties config = new Properties();
    private static String configPath = "";

    private static Properties getConfiguration() throws IOException {
        if (config.isEmpty()) {
            loadConfiguration();
        }
        return config;
    }

    public static void setConfigPath(String path) {
        configPath = path;
    }

    public static String getConfigPath() {
        return configPath;
    }

    private static void loadConfiguration() {
        InputStream configFile;
        try {
            if (configPath == null || configPath.isEmpty())
                configFile = ConfigurationPropertiesUtil.class.getClassLoader()
                        .getResourceAsStream(Constants.DEFAULT_CONFIG_PATH_XML);
            else {
                configFile = ConfigurationPropertiesUtil.class.getClassLoader()
                        .getResourceAsStream(configPath);
            }

            config.loadFromXML(configFile);
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }

    }

    public static String getEnvironmentVariable(String key) {
        try {
            return getConfiguration().getProperty(key);
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static String[] getEnvironmentVariableList(String key) {
        try {
            String value = getConfiguration().getProperty(key);
            if (value != null)
                return getConfiguration().getProperty(key).split(",");
            return null;
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static HashMap<String, String> getEnvironmentMapVariable(String key) {
        HashMap<String, String> mapVariable = new HashMap<>();
        try {
            String keyValue = getConfiguration().getProperty(key);
            String[] pairs = keyValue.split(",");

            for (String el : pairs) {
                String[] keyValueStr = el.split(":");
                mapVariable.put(keyValueStr[0], keyValueStr[1]);
            }

            return mapVariable;
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }

        return null;
    }
}
