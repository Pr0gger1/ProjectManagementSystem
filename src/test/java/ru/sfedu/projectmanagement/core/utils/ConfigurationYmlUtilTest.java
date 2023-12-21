package ru.sfedu.projectmanagement.core.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanagement.core.utils.config.ConfigYmlUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationYmlUtilTest {
    private static final String[] expectedArray = { "Earth", "Mars", "Saturn", "Venus" };



    @BeforeEach
    public void resetPath() {
        ConfigYmlUtil.setConfigPath("env.yml");
    }

    @Test
    void setConfigPath() {
        ConfigYmlUtil.setConfigPath("test.yml");
        assertEquals("test.yml", ConfigYmlUtil.getConfigPath());
    }

    @Test
    void getConfigPath() {
        assertEquals("env.yml", ConfigYmlUtil.getConfigPath());
    }

    @Test
    void getEnvironmentVariable() {
        Object data = ConfigYmlUtil.getEnvironmentVariable("database");
        Map<String, String> expectedData = new HashMap<>();
        expectedData.put("login", "login");

        assertEquals(expectedData, data);
    }

    @Test
    void getEnvironmentVariableThroughPoint() {
        Object data = ConfigYmlUtil.getEnvironmentVariable("database.login");
        assertEquals("login", data);
    }

    @Test
    void getEnvironmentVariableList() {
        String[] actualArray = ConfigYmlUtil.getEnvironmentVariableList("planets");
        assertArrayEquals(expectedArray, actualArray);
    }
}