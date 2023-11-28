package ru.sfedu.projectmanager;

public class Constants {
    // property file formats
    public static final String DEFAULT_CONFIG_PATH_PROPERTIES = "env.properties";
    public static final String DEFAULT_CONFIG_PATH_XML = "env.xml";
    public static final String DEFAULT_CONFIG_PATH_YML = "env.yml";


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
    public static final String MONGO_DB_TEST = "history_test";
    public static final String MONGO_DB_REAL = "history";


    // datasource path
    public static final String DATASOURCE_PATH_XML = "data/xml/";
    public static final String DATASOURCE_PATH_CSV = "data/csv/";

    // postgres database properties variables
    public static final String POSTGRES_URL = "POSTGRES_URL";
    public static final String POSTGRES_USER = "POSTGRES_USER";
    public static final String POSTGRES_PASSWORD = "POSTGRES_PASSWORD";
    public static final String POSTGRES_PROD_DB_NAME = "POSTGRES_PROD_DB_NAME";
    public static final String POSTGRES_TEST_DB_NAME = "POSTGRES_TEST_DB_NAME";

    public static final String PROJECT_TABLE_NAME = "projects";
    public static final String PROJECT_EMPLOYEE_TABLE_NAME = "employee_project";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String BUG_REPORTS_TABLE_NAME = "bug_reports";
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String DOCUMENTATIONS_TABLE_NAME = "documentations";
    public static final String EMPLOYEES_TABLE_NAME = "employees";

    public static final String INIT_PROJECT_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS projects (
            id VARCHAR(64) PRIMARY KEY,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            status VARCHAR(16) DEFAULT 'IN_PROGRESS',
            deadline TIMESTAMP,
            manager_id UUID REFERENCES employees(id)
        );
    """;

    public static final String INIT_PROJECT_EMPLOYEE_TABLE_QUERY = """
       CREATE TABLE IF NOT EXISTS employee_project (
            employee_id UUID NOT NULL,
            project_id VARCHAR(64) NOT NULL,
            PRIMARY KEY(employee_id, project_id),
            FOREIGN KEY(employee_id) REFERENCES employees(id) ON DELETE CASCADE,
            FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE
       );
    """;

    public static final String INIT_TASK_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS tasks (
            id UUID PRIMARY KEY,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            executor_id UUID NOT NULL REFERENCES employees(id),
            executor_full_name VARCHAR(128) NOT NULL,
            comment TEXT,
            priority VARCHAR(20) DEFAULT 'UNDEFINED',
            tag VARCHAR(128),
            status VARCHAR(16) DEFAULT 'IN_PROGRESS',
            deadline TIMESTAMP,
            created_at DATE DEFAULT NOW()
        );
    """;

    public static final String INIT_BUG_REPORT_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS bug_reports (
            id UUID PRIMARY KEY,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            status VARCHAR(16) DEFAULT 'OPENED',
            priority VARCHAR(20) DEFAULT 'UNDEFINED',
            name VARCHAR(128) NOT NULL,
            description TEXT,
            author_id UUID NOT NULL REFERENCES employees(id),
            author_full_name VARCHAR(128) NOT NULL,
            created_at DATE DEFAULT NOW()
        );
    """;

    public static final String INIT_EVENT_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS events (
            id UUID PRIMARY KEY,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            author_id UUID NOT NULL REFERENCES employees(id),
            author_full_name VARCHAR(128) NOT NULL,
            start_date TIMESTAMP NOT NULL,
            end_date TIMESTAMP NOT NULL,
            created_at DATE DEFAULT NOW()
        );
    """;

    public static final String INIT_DOCUMENTATION_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS documentations (
            id UUID PRIMARY KEY,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            author_id UUID NOT NULL REFERENCES employees(id),
            employee_full_name VARCHAR(128) NOT NULL,
            body TEXT,
            created_at DATE DEFAULT NOW()
        );
    """;

    public static final String INIT_EMPLOYEE_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS employees (
            id UUID PRIMARY KEY,
            first_name VARCHAR(64) NOT NULL,
            last_name VARCHAR(64) NOT NULL,
            patronymic VARCHAR(64),
            birthday DATE,
            email VARCHAR(64),
            phone_number VARCHAR(32),
            position VARCHAR(64) NOT NULL
        );
    """;

    public static final String CREATE_PROJECT_QUERY = """
        INSERT INTO projects (id, name, description, status, deadline, manager_id)
        VALUES (?, ?, ?, ?, ?, ?);
    """;

    public static final String CREATE_EMPLOYEE_QUERY = """
        INSERT INTO employees (id, first_name, last_name, patronymic, birthday, email, phone_number, position)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
    """;

    public static final String CREATE_TASK_QUERY = """
        INSERT INTO tasks (id, project_id, name, description, executor_id, executor_full_name, comment, priority, tag, status, deadline, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """;

    public static final String CREATE_BUG_REPORT_QUERY = """
        INSERT INTO bug_reports (id, project_id, status, priority, name, description, author_id, author_full_name, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """;

    public static final String CREATE_EVENT_QUERY = """
        INSERT INTO events (id, name, description, project_id, author_id, author_full_name, start_date, end_date, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """;

    public static final String CREATE_DOCUMENTATION_QUERY = """
        INSERT INTO documentations (id, name, description, project_id, author_id, employee_full_name, body, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
    """;

    public static final String CREATE_EMPLOYEE_PROJECT_LINK_QUERY = """
        INSERT INTO employee_project (employee_id, project_id)
        VALUES (?, ?);
    """;

    public static final String DELETE_PROJECT_QUERY = """
        DELETE FROM projects WHERE id = ?;
    """;

    public static final String DELETE_TASK_QUERY = """
        DELETE FROM tasks where id = ?;
    """;
}
