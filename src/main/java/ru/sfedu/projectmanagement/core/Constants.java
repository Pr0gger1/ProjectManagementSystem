package ru.sfedu.projectmanagement.core;

public class Constants {
    // property file formats
    public static final String DEFAULT_CONFIG_PATH_PROPERTIES = "env.properties";
    public static final String DEFAULT_CONFIG_PATH_XML = "env.xml";
    public static final String DEFAULT_CONFIG_PATH_YML = "env.yml";


    // file database extensions
    public static final String FILE_XML_EXTENSION = ".xml";
    public static final String FILE_CSV_EXTENSION = ".csv";
    public static final String CSV_DEFAULT_SEPARATOR = ";";


    // xml and csv entity filenames
    public static final String PROJECTS_FILE_PATH = "projects";
    public static final String EMPLOYEES_FILE_PATH = "employees";
    public static final String TASKS_FILE_PATH = "tasks";
    public static final String BUG_REPORTS_FILE_PATH = "bug_reports";
    public static final String EVENTS_FILE_PATH = "events";
    public static final String DOCUMENTATIONS_FILE_PATH = "documentations";
    public static final String EMPLOYEE_PROJECT_FILE_PATH = "employee_project";


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

    // properties variables
    public static final String MONGO_URL = "MONGO_URL";
    public static final String POSTGRES_URL = "POSTGRES_URL";
    public static final String POSTGRES_USER = "POSTGRES_USER";
    public static final String POSTGRES_PASSWORD = "POSTGRES_PASSWORD";
    public static final String POSTGRES_PROD_DB_NAME = "POSTGRES_PROD_DB_NAME";
    public static final String POSTGRES_TEST_DB_NAME = "POSTGRES_TEST_DB_NAME";
    public static final String ENVIRONMENT = "ENVIRONMENT";
    public static final String DATABASE_TYPE = "DATABASE_TYPE";


    // postgres table names
    public static final String PROJECT_TABLE_NAME = "projects";
    public static final String EMPLOYEE_PROJECT_TABLE_NAME = "employee_project";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String BUG_REPORTS_TABLE_NAME = "bug_reports";
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String DOCUMENTATIONS_TABLE_NAME = "documentations";
    public static final String EMPLOYEES_TABLE_NAME = "employees";


    // init postgres tables queries
    public static final String INIT_PROJECT_TABLE_QUERY = String.format("""
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(64) PRIMARY KEY,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            version VARCHAR,
            status VARCHAR(16) DEFAULT 'IN_PROGRESS',
            deadline TIMESTAMPTZ,
            manager_id UUID REFERENCES employees(id) ON DELETE CASCADE
        );
    """, PROJECT_TABLE_NAME);

    public static final String INIT_PROJECT_EMPLOYEE_TABLE_QUERY = String.format("""
       CREATE TABLE IF NOT EXISTS %s (
            employee_id UUID NOT NULL,
            project_id VARCHAR(64) NOT NULL,
            PRIMARY KEY(employee_id, project_id),
            FOREIGN KEY(employee_id) REFERENCES employees(id) ON DELETE CASCADE,
            FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE
       );
    """, EMPLOYEE_PROJECT_TABLE_NAME);

    public static final String INIT_TASK_TABLE_QUERY = String.format("""
        CREATE TABLE IF NOT EXISTS %s (
            id UUID PRIMARY KEY,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            executor_id UUID NOT NULL REFERENCES employees(id),
            executor_full_name VARCHAR(128) NOT NULL,
            comment TEXT,
            priority VARCHAR(20) DEFAULT 'UNDEFINED',
            tag VARCHAR(32)[],
            status VARCHAR(16) DEFAULT 'IN_PROGRESS',
            deadline TIMESTAMPTZ,
            created_at TIMESTAMPTZ DEFAULT NOW(),
            completed_at TIMESTAMPTZ DEFAULT NOW()
        );
    """, TASKS_TABLE_NAME);

    public static final String INIT_BUG_REPORT_TABLE_QUERY = String.format("""
        CREATE TABLE IF NOT EXISTS %s (
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
    """, BUG_REPORTS_TABLE_NAME);

    public static final String INIT_EVENT_TABLE_QUERY = String.format("""
        CREATE TABLE IF NOT EXISTS %s (
            id UUID PRIMARY KEY,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            author_id UUID NOT NULL REFERENCES employees(id),
            author_full_name VARCHAR(128) NOT NULL,
            start_date TIMESTAMPTZ NOT NULL,
            end_date TIMESTAMPTZ NOT NULL,
            created_at TIMESTAMPTZ DEFAULT NOW()
        );
    """, EVENTS_TABLE_NAME);

    public static final String INIT_DOCUMENTATION_TABLE_QUERY = String.format("""
        CREATE TABLE IF NOT EXISTS %s (
            id UUID PRIMARY KEY,
            project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
            name VARCHAR(128) NOT NULL,
            description TEXT,
            author_id UUID NOT NULL REFERENCES employees(id),
            author_full_name VARCHAR(128) NOT NULL,
            article_titles text[],
            articles text[],
            created_at DATE DEFAULT NOW()
        );
    """, DOCUMENTATIONS_TABLE_NAME);

    public static final String INIT_EMPLOYEE_TABLE_QUERY = String.format("""
        CREATE TABLE IF NOT EXISTS %s (
            id UUID PRIMARY KEY,
            first_name VARCHAR(64) NOT NULL,
            last_name VARCHAR(64) NOT NULL,
            patronymic VARCHAR(64),
            birthday DATE,
            email VARCHAR(64),
            phone_number VARCHAR(32),
            position VARCHAR(64) NOT NULL
        );
    """, EMPLOYEES_TABLE_NAME);


    // postgres create entity queries
    public static final String CREATE_PROJECT_QUERY = String.format("""
        INSERT INTO %s (id, name, description, status, deadline, manager_id)
        VALUES (?, ?, ?, ?, ?, ?);
    """, PROJECT_TABLE_NAME);

    public static final String CREATE_EMPLOYEE_QUERY = String.format("""
        INSERT INTO %s (id, first_name, last_name, patronymic, birthday, email, phone_number, position)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
    """, EMPLOYEES_TABLE_NAME);

    public static final String CREATE_TASK_QUERY = String.format("""
        INSERT INTO %s (id, project_id, name, description, executor_id, executor_full_name, comment, priority, tag, status, deadline, created_at, completed_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """, TASKS_TABLE_NAME);

    public static final String CREATE_BUG_REPORT_QUERY = String.format("""
        INSERT INTO %s (id, project_id, status, priority, name, description, author_id, author_full_name, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """, BUG_REPORTS_TABLE_NAME);

    public static final String CREATE_EVENT_QUERY = String.format("""
        INSERT INTO %s (id, name, description, project_id, author_id, author_full_name, start_date, end_date, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """, EVENTS_TABLE_NAME);

    public static final String CREATE_DOCUMENTATION_QUERY = String.format("""
        INSERT INTO %s (id, name, description, project_id, author_id, author_full_name, article_titles, articles, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """, DOCUMENTATIONS_TABLE_NAME);

    public static final String CREATE_EMPLOYEE_PROJECT_LINK_QUERY = String.format("""
        INSERT INTO %s (employee_id, project_id)
        VALUES (?, ?);
    """, EMPLOYEE_PROJECT_TABLE_NAME);


    // postgres delete entity query
    public static final String DELETE_ENTITY_QUERY = """
        DELETE FROM %s WHERE id = ?;
    """;


    // postgres update entity queries
    public static final String UPDATE_COLUMN_ENTITY_QUERY = """
        UPDATE %s SET %s = ? WHERE id = ?
    """;

    public static final String UPDATE_PROJECT_QUERY = String.format(
        "UPDATE %s SET name = ?, description = ?, status = ?, deadline = ?, manager_id = ? WHERE id = ?;",
        PROJECT_TABLE_NAME
    );

    public static final String UPDATE_EMPLOYEE_QUERY = String.format("""
        UPDATE %s SET first_name = ?, last_name = ?, patronymic = ?, birthday = ?,
         email = ?, phone_number = ?, position = ? WHERE id = ?;
    """, EMPLOYEES_TABLE_NAME);

    public static final String UPDATE_TASK_QUERY = String.format("""
        UPDATE %s SET project_id = ?,
                    name = ?,
                    description = ?,
                    executor_id = ?,
                    executor_full_name = ?,
                    comment = ?,
                    priority = ?,
                    tag = ?,
                    status = ?,
                    deadline = ?,
                    created_at = ?,
                    completed_at = ?
                WHERE id = ?;
    """, TASKS_TABLE_NAME);

    public static final String UPDATE_EMPLOYEE_PROJECT_QUERY = String.format("""
        UPDATE %s SET employee_id = ? WHERE project_id = ?;
    """, EMPLOYEE_PROJECT_TABLE_NAME);

    public static final String UPDATE_BUG_REPORT_QUERY = String.format("""
        UPDATE %s
                SET project_id = ?,
                    status = ?,
                    priority = ?,
                    name = ?,
                    description = ?,
                    author_id = ?,
                    author_full_name = ?,
                    created_at = ?
                WHERE id = ?;
    """, BUG_REPORTS_TABLE_NAME);

    public static final String UPDATE_EVENT_QUERY = String.format("""
            UPDATE %s
                    SET name = ?,
                        description = ?,
                        project_id = ?,
                        author_id = ?,
                        author_full_name = ?,
                        start_date = ?,
                        end_date = ?,
                        created_at = ?
                    WHERE id = ?;
    """, EVENTS_TABLE_NAME);

    public static final String UPDATE_DOCUMENTATION_QUERY = String.format("""
            UPDATE %s
            SET project_id = ?,
                name = ?,
                description = ?,
                author_id = ?,
                author_full_name = ?,
                article_titles = ?,
                articles = ?,
                created_at = ?
            WHERE id = ?;
                
    """, DOCUMENTATIONS_TABLE_NAME);

    public static final String UPDATE_TASK_EXECUTOR_QUERY = String.format("""
        UPDATE %s SET executor_id = ?, executor_full_name = ? WHERE id = ? AND project_id = ?
    """, TASKS_TABLE_NAME);

    public static final String GET_ENTITY_BY_ID_QUERY = """
        SELECT * FROM %s WHERE id = ?
    """;

    public static final String GET_ENTITY_BY_PROJECT_ID_QUERY = """
        SELECT * FROM %s WHERE project_id = ?
    """;
    public static final String GET_PROJECT_TEAM_QUERY = String.format("""
        SELECT p.* FROM %s ep JOIN %s p ON ep.employee_id = p.id;
    """, EMPLOYEE_PROJECT_TABLE_NAME, EMPLOYEES_TABLE_NAME);

    public static final String GET_TASKS_BY_EMPLOYEE_ID_QUERY = String.format("""
        SELECT * FROM %s WHERE executor_id = ?
    """, TASKS_TABLE_NAME);

    public static final String TRACK_INFO_KEY_LABOR_EFFICIENCY = "labor_efficiency";
    public static final String TRACK_INFO_KEY_PROJECT_READINESS = "project_readiness";
    public static final String TRACK_INFO_KEY_TASK_STATUS = "task_status";
    public static final String TRACK_INFO_KEY_BUG_STATUS = "bug_status";
}
