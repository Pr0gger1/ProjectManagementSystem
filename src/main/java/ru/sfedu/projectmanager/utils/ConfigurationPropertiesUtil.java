package ru.sfedu.projectmanager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanager.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class ConfigurationPropertiesUtil implements ConfigurationUtil {
    private static final Logger logger = LogManager.getLogger(ConfigurationPropertiesUtil.class);
    private static final Properties config = new Properties();
    private static String configPath = "";

    private Properties getConfiguration() throws IOException {
        if (config.isEmpty()) {
            loadConfiguration();
        }
        return config;
    }

    public void setConfigPath(String path) {
        configPath = path;
    }

    public String getConfigPath() {
        return configPath;
    }

    private void loadConfiguration() throws IOException {
        InputStream fileStream = null;
        try {
            if (configPath == null || configPath.isEmpty())
                fileStream = ConfigurationPropertiesUtil.class.getClassLoader()
                        .getResourceAsStream(Constants.DEFAULT_CONFIG_PATH_PROPERTIES);
            else {
                fileStream = ConfigurationPropertiesUtil.class.getClassLoader()
                        .getResourceAsStream(configPath);
            }

            config.load(fileStream);
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }
        finally {
            assert fileStream != null;
            fileStream.close();
        }

    }

    public String getEnvironmentVariable(String key) {
        try {
            return getConfiguration().getProperty(key);
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public String[] getEnvironmentVariableList(String key) {
        try {
            return getConfiguration().getProperty(key).split(",");
        }
        catch (IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public HashMap<String, String> getEnvironmentMapVariable(String key) {
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
