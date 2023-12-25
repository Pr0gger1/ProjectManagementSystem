package ru.sfedu.projectmanagement.core;

import ru.sfedu.projectmanagement.core.model.*;

public class Constants {
    // log messages
    public static final String SUCCESSFUL_CREATED_ENTITY_MESSAGE = "%s %s was created successfully";
    public static final String SUCCESSFUL_DELETED_ENTITY_MESSAGE = "%s %s was deleted successfully";
    public static final String ENTITY_NOT_FOUND_MESSAGE = "%s with id %s not found";
    public static final String READ_ERROR = "something goes wrong while reading...";
    public static final String OBJECT_ALREADY_EXISTS_MESSAGE = "%s with id %s already exists";

    public static final String UUID_REGEX = "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}";

    // property file formats
    public static final String DEFAULT_CONFIG_PATH_PROPERTIES = "env.properties";
    public static final String DEFAULT_CONFIG_PATH_XML = "env.xml";
    public static final String DEFAULT_CONFIG_PATH_YML = "env.yml";


    // file database extensions
    public static final String FILE_XML_EXTENSION = ".xml";
    public static final String FILE_CSV_EXTENSION = ".csv";


    // xml and csv entity filenames
    public static final String PROJECTS_FILE_PATH = "projects";
    public static final String EMPLOYEES_FILE_PATH = "employees";
    public static final String TASKS_FILE_PATH = "tasks";
    public static final String BUG_REPORTS_FILE_PATH = "bug_reports";
    public static final String EVENTS_FILE_PATH = "events";
    public static final String DOCUMENTATIONS_FILE_PATH = "documentations";

    // filenames for supportive entities
    public static final String EMPLOYEE_PROJECT_FILE_PATH = "employee_project";
    public static final String TASK_TAG_FILE_PATH = "task_tags";
    public static final String DOCUMENTATION_DATA_FILE_PATH = "documentation_data";
    public static final String MANAGER_PROJECT_FILE_PATH = "manager_employee";


    // mongo history collection item name constants
    public static final String MONGO_HISTORY_ID = "Id";
    public static final String MONGO_HISTORY_CLASSNAME = "Class name";
    public static final String MONGO_HISTORY_CREATED_AT = "Created at";
    public static final String MONGO_HISTORY_ACTOR = "System";
    public static final String MONGO_HISTORY_METHOD_NAME = "Method name";
    public static final String MONGO_HISTORY_OBJECT = "Object";
    public static final String MONGO_HISTORY_STATUS = "Status";
    public static final String MONGO_HISTORY_CHANGE_TYPE = "Change type";

    // mongo database names
    public static final String MONGO_DB_NAME_TEST = "history_test";
    public static final String MONGO_DB_NAME_PRODUCTION = "history";

    // datasource path
    public static final String DATASOURCE_PATH_XML = "data/xml/";
    public static final String DATASOURCE_PATH_CSV = "data/csv/";
    public static final String DATASOURCE_TEST_PATH_XML = "src/test/data/xml/";
    public static final String DATASOURCE_TEST_PATH_CSV = "src/test/data/csv/";

    // properties variables
    public static final String MONGO_URL = "MONGO_URL";
    public static final String POSTGRES_URL = "POSTGRES_URL";
    public static final String POSTGRES_USER = "POSTGRES_USER";
    public static final String POSTGRES_PASSWORD = "POSTGRES_PASSWORD";
    public static final String POSTGRES_PROD_DB_NAME = "POSTGRES_PROD_DB_NAME";
    public static final String POSTGRES_TEST_DB_NAME = "POSTGRES_TEST_DB_NAME";
    public static final String ENVIRONMENT = "ENVIRONMENT";


    // validation keys
    public static final String PROJECT_ERROR_KEY = "project";
    public static final String EMPLOYEE_ERROR_KEY = "employee";
    public static final String BUG_REPORT_ERROR_KEY = "bug report";
    public static final String TASK_ERROR_KEY = "task";

    // validation messages
    public static final String PROJECT_DOES_NOT_EXISTS = "project with id %s doesn't exist";
    public static final String EMPLOYEE_DOES_NOT_EXISTS = "employee with id %s doesn't exist";
    public static final String EMPLOYEE_INVALID_NAME = "invalid name";
    public static final String EMPLOYEE_INVALID_LAST_NAME = "invalid last name";
    public static final String EMPLOYEE_INVALID_POSITION = "invalid position value";
    public static final String TASK_DOES_NOT_EXISTS = "task with id %s doesn't exist";
    public static final String ENTITY_INVALID_NAME = "invalid name";
    public static final String EMPLOYEE_IS_NULL = "employee should be defined";
    public static final String EMPLOYEE_INVALID_FULL_NAME = "invalid employee's full name";
    public static final String EMPLOYEE_IS_NOT_LINKED_TO_PROJECT = "employee with id %s doesn't belong to the project";

    public static final String INVALID_PARAMETERS_MESSAGE = "Invalid count of parameters";

    public static final int PROJECT_PRIMITIVE_PARAMETER_COUNT = Project.class.getDeclaredFields().length - 7;
    public static final int TASK_PRIMITIVE_PARAMETER_COUNT = Task.class.getDeclaredFields().length
            + Task.class.getSuperclass().getDeclaredFields().length - 4;
    public static final int BUG_REPORT_PRIMITIVE_PARAMETER_COUNT = BugReport.class.getDeclaredFields().length
            + BugReport.class.getSuperclass().getDeclaredFields().length - 3;
    public static final int EMPLOYEE_PRIMITIVE_PARAMETER_COUNT = Employee.class.getDeclaredFields().length - 3;
    public static final int EVENT_PRIMITIVE_PARAMETER_COUNT = Event.class.getDeclaredFields().length
            + Event.class.getSuperclass().getDeclaredFields().length - 3;
    public static final int DOCUMENTATION_PRIMITIVE_PARAMETER_COUNT = Documentation.class.getDeclaredFields().length
            + Documentation.class.getSuperclass().getDeclaredFields().length - 4;
}
