package ru.sfedu.projectmanagement.core.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanagement.core.utils.config.ConfigXmlUtil;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationXmlUtilTest {
    private static final HashMap<String, String> expectedMapVariable = new HashMap<>();
    private static final String[] expectedArray = { "Earth", "Mars", "Saturn", "Venus" };

    @BeforeAll
    static void prepareData() {
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

    @BeforeEach
    public void resetPath() {
        ConfigXmlUtil.setConfigPath("env.xml");
    }

    @Test
    void setConfigPath() {
        ConfigXmlUtil.setConfigPath("test.xml");
        assertEquals("test.xml", ConfigXmlUtil.getConfigPath());
    }

    @Test
    void getConfigPath() {
        assertEquals("env.xml", ConfigXmlUtil.getConfigPath());
    }



    @Test
    void getEnvironmentVariable() {
        String variable = ConfigXmlUtil.getEnvironmentVariable("dbpassword");
        assertEquals("12345", variable);
    }

    @Test
    void getEnvironmentVariableList() {
        String[] actualArray = ConfigXmlUtil.getEnvironmentVariableList("PLANETS");
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getEnvironmentMapVariable() {
        HashMap<String, String> actualMapVariable = ConfigXmlUtil
                .getEnvironmentMapVariable("MONTHS");

        assertEquals(expectedMapVariable, actualMapVariable);
    }
}