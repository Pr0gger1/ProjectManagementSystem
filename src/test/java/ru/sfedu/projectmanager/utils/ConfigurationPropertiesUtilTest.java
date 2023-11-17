package ru.sfedu.projectmanager.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationPropertiesUtilTest {
    private static final HashMap<String, String> expectedMapVariable = new HashMap<>();
    private static final String[] expectedArray = { "Earth", "Mars", "Saturn", "Venus" };
    private static final ConfigurationPropertiesUtil configProvider = new ConfigurationPropertiesUtil();


    @BeforeAll
    static void prepareData() {
        configProvider.setConfigPath("test.properties");
        expectedMapVariable.put("1", "January");
        expectedMapVariable.put("2", "February");
        expectedMapVariable.put("3", "March");
        expectedMapVariable.put("4", "April");
        expectedMapVariable.put("5", "May");
        expectedMapVariable.put("6", "June");
        expectedMapVariable.put("7", "July");
        expectedMapVariable.put("8", "August");
        expectedMapVariable.put("9", "September");
        expectedMapVariable.put("10", "October");
        expectedMapVariable.put("11", "November");
        expectedMapVariable.put("12", "December");
    }


    @Test
    void setConfigPath() {
        configProvider.setConfigPath("env.properties");
        assertEquals("env.properties", configProvider.getConfigPath());
        configProvider.setConfigPath("test.properties");
    }

    @Test
    void getConfigPath() {
        assertEquals("test.properties", configProvider.getConfigPath());
    }


    @Test
    void getEnvironmentVariable() {
        String dbName = configProvider.getEnvironmentVariable("DB_NAME");
        assertEquals("projectManagerDB", dbName);
    }

    @Test
    void getEnvironmentVariableList() {
        String[] actualArray = configProvider.getEnvironmentVariableList("PLANETS");
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getEnvironmentMapVariable() {
        HashMap<String, String> actualMapVariable = configProvider
                .getEnvironmentMapVariable("MONTHS");

        assertEquals(expectedMapVariable, actualMapVariable);
    }
}