package ru.sfedu.projectmanagement.core.utils.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class ConfigPropertiesUtil {
    private static final Logger logger = LogManager.getLogger(ConfigPropertiesUtil.class);
    private static final Properties config = new Properties();
    private static String configPath = "";

    private static Properties getConfiguration() throws IOException {
        if (config.isEmpty()) {
            loadConfiguration();
        }
        return config;
    }

    /**
     * @param path
     */
    public static void setConfigPath(String path) {
        configPath = path;
    }

    /**
     * @return
     */
    public static String getConfigPath() {
        return configPath;
    }

    /**
     */
    private static void loadConfiguration() throws FileNotFoundException {
        InputStream inputStream;
        if (configPath == null || configPath.isEmpty()) {
            inputStream = ConfigPropertiesUtil.class.getClassLoader()
                    .getResourceAsStream(Constants.DEFAULT_CONFIG_PATH_PROPERTIES);
        }
        else {
            File configFile = new File(configPath);
            inputStream = new FileInputStream(configFile);
        }
        try {
            config.load(inputStream);
            if (inputStream != null)
                inputStream.close();
        }
        catch (IOException error) {
            logger.error("loadConfiguration[1]: {}", error.getMessage());
        }
    }

    /**
     * @param key
     * @return
     */
    public static String getEnvironmentVariable(String key) {
        try {
            return getConfiguration().getProperty(key);
        }
        catch (IOException error) {
            logger.error("getEnvironment[1]: {}", error.getMessage());
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
    public static String[] getEnvironmentVariableList(String key) {
        try {
            return getConfiguration().getProperty(key).split(",");
        }
        catch (IOException error) {
            logger.error("getEnvironmentVariableList[1]: {}", error.getMessage());
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
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
            logger.error("getEnvironmentMapVariable[1]: {}", error.getMessage());
        }

        return null;
    }
}
