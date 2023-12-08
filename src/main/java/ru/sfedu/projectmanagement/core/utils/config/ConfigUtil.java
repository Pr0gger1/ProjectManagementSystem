package ru.sfedu.projectmanagement.core.utils.config;

import java.util.Map;

public interface ConfigUtil {
    private static Object getConfiguration() {
        return null;
    }

    static void setConfigPath(String path) {}

    static String getConfigPath() {
        return null;
    }

    private static void loadConfiguration() {}

    static String getEnvironmentVariable(String key) {
        return null;
    }
    static String[] getEnvironmentVariableList(String key) { return null; }
    static Map<String, String> getEnvironmentMapVariable(String key) { return null; }
}
