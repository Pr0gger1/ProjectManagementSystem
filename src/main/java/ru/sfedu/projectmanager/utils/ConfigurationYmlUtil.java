package ru.sfedu.projectmanager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import ru.sfedu.projectmanager.Constants;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ConfigurationYmlUtil implements ConfigurationUtil {
    private static final Yaml config = new Yaml();
    private static HashMap<String, Object> data;
    private static String configPath = "";

    public static void setConfigPath(String path) {
        configPath = path;
    }

    public static String getConfigPath() {
        return configPath;
    }

    private static void loadConfiguration() {
        InputStream configFile = null;
        if (configPath == null || configPath.isEmpty())
            configFile = ConfigPropertiesUtil.class.getClassLoader()
                    .getResourceAsStream(Constants.DEFAULT_CONFIG_PATH_YML);
        else {
            configFile = ConfigPropertiesUtil.class.getClassLoader()
                    .getResourceAsStream(configPath);
        }

        data = config.load(configFile);

    }

    public static Object getEnvironmentVariable(String key) {
        loadConfiguration();

        if (key.contains(".")) {
            String[] keyValue = key.split("\\.");
            Object current = data.get(keyValue[0]);

            for (int i = 1; i < keyValue.length; i++) {
                current = ((LinkedHashMap<String, String>)current).get(keyValue[i]);
            }
            return current;
        }
        return data.get(key);
    }

    public static String[] getEnvironmentVariableList(String key) {
        loadConfiguration();

        Object value = data.get(key);
        if (value != null)
            return ((String) value).split(",");
        return null;
    }

    public static HashMap<String, String> getEnvironmentMapVariable(String key) {
        loadConfiguration();
        Object keyValue = data.get(key);
        if (keyValue instanceof Map) {
            return (HashMap<String, String>) keyValue;
        }
        return null;
    }
}
