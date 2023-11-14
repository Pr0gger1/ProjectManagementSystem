package ru.sfedu.projectmanager;

public class Constants {
    // property file formats
    public static final String DEFAULT_CONFIG_PROPERTIES_PATH = "env.properties";
    public static final String DEFAULT_CONFIG_XML_PATH = "env.xml";
    public static final String DEFAULT_CONFIG_YML_PATH = "env.yml";


    // mongo history collection item name constants
    public static final String ACTOR = "System";
    public static final String MONGO_HISTORY_ID = "Id";
    public static final String MONGO_HISTORY_CLASSNAME = "Class name";
    public static final String MONGO_HISTORY_CREATED_AT = "Created at";
    public static final String MONGO_HISTORY_ACTOR = "Actor";
    public static final String MONGO_HISTORY_METHOD_NAME = "Method name";
    public static final String MONGO_HISTORY_OBJECT = "Object";
    public static final String MONGO_HISTORY_STATUS = "Status";
    public static final String MONGO_HISTORY_CHANGE_TYPE = "Change type";

    // mongo database names
    public static final String MONGO_TEST_DB = "history_test";
    public static final String MONGO_REAL_DB = "history";


    // datasource path
    public static final String DATA_XML_PATH = "data/xml";
    public static final String DATA_CSV_PATH = "data/csv";

}
