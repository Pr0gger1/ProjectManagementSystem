package ru.sfedu.projectmanager.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationYmlUtilTest {
    private static final String[] expectedArray = { "Earth", "Mars", "Saturn", "Venus" };



    @BeforeEach
    public void resetPath() {
        ConfigurationYmlUtil.setConfigPath("env.yml");
    }

    @Test
    void setConfigPath() {
        ConfigurationYmlUtil.setConfigPath("test.yml");
        assertEquals("test.yml", ConfigurationYmlUtil.getConfigPath());
    }

    @Test
    void getConfigPath() {
        assertEquals("env.yml", ConfigurationYmlUtil.getConfigPath());
    }
{
    }

    @Test
    void getEnvironmentVariable() {
        Object data = ConfigurationYmlUtil.getEnvironmentVariable("database");
        Map<String, String> expectedData = new HashMap<>();
        expectedData.put("login", "login");

        assertEquals(expectedData, data);
    }

    @Test
    void getEnvironmentVariableThroughPoint() {
        Object data = ConfigurationYmlUtil.getEnvironmentVariable("database.login");
        assertEquals("login", data);
    }

    @Test
    void getEnvironmentVariableList() {
        String[] actualArray = ConfigurationYmlUtil.getEnvironmentVariableList("planets");
        assertArrayEquals(expectedArray, actualArray);
    }
}