package ru.sfedu.projectmanager;

import ru.sfedu.projectmanager.utils.ConfigurationPropertiesUtil;

public class Constants {
    public static final String DEFAULT_CONFIG_PROPERTIES_PATH = "env.properties";
    public static final String DEFAULT_CONFIG_XML_PATH = "env.xml";
    public static final String DEFAULT_CONFIG_YML_PATH = "env.yml";
    public static final ConfigurationPropertiesUtil configPropertiesProvider = new ConfigurationPropertiesUtil();

    public static final String ACTOR = "System";

    // mongo history collection item name constants
    public static final String MONGO_HISTORY_ID = "Id";
    public static final String MONGO_HISTORY_CLASSNAME = "ClassName";
    public static final String MONGO_HISTORY_CREATED_AT = "CreatedAt";
    public static final String MONGO_HISTORY_ACTOR = "Actor";
    public static final String MONGO_HISTORY_METHOD_NAME = "MethodName";
    public static final String MONGO_HISTORY_OBJECT = "Object";
    public static final String MONGO_HISTORY_STATUS = "Status";

    public static final String MONGO_TEST_DB = "history_test";
    public static final String MONGO_REAL_DB = "history";
}
