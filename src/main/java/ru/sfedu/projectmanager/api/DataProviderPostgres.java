package ru.sfedu.projectmanager.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;
import ru.sfedu.projectmanager.utils.ConfigPropertiesUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class DataProviderPostgres implements IDataProvider {
    private final Logger logger = LogManager.getLogger(DataProviderPostgres.class);
    private Connection connection = null;
    private final Environment dbEnvironment;
    private String dbName;

    public DataProviderPostgres() {
        dbEnvironment = Environment.PRODUCTION;
        initProvider();
    }

    public DataProviderPostgres(Environment environment) {
        dbEnvironment = environment;
        initProvider();
    }

    /**
     * initializes all entity tables before executing queries
     */
    private void initProvider() {
        // define db name (prod or test)
        setDbName();
        // getting database connection
        connection = getConnection();
        // create tables if not exists
        initDatabaseTables();
    }

    /**
     * set database name for test or production environment
     */
    private void setDbName() {
        dbName = dbEnvironment == Environment.TEST ?
                ConfigPropertiesUtil
                        .getEnvironmentVariable(Constants.POSTGRES_TEST_DB_NAME) :
                ConfigPropertiesUtil
                        .getEnvironmentVariable(Constants.POSTGRES_PROD_DB_NAME);
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * @return enum Environment value
     */
    public Environment getDbEnvironment() {
        return dbEnvironment;
    }

    /**
     * @return connection instance to database
     */
    public Connection getConnection() {
         String dbName = getDbName();
         String dbUrl = ConfigPropertiesUtil.getEnvironmentVariable(Constants.POSTGRES_URL) + dbName;
         String dbUser = ConfigPropertiesUtil.getEnvironmentVariable(Constants.POSTGRES_USER);
         String dbPassword = ConfigPropertiesUtil.getEnvironmentVariable(Constants.POSTGRES_PASSWORD);

         logger.debug("getConnection[1]: dbUrl = {}", dbUrl);
         logger.debug("getConnection[2]: dbUser = {}", dbUser);
         logger.debug("getConnection[3]: dbUrl = {}", dbPassword);

         try {
             connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             logger.debug("getConnection[4]: successful connection");
         }
         catch (SQLException exception) {
             logger.error("getConnection[5]: database error {}", exception.getMessage());
         }

         return connection;
     }

     private void initDatabaseTables() {
         String[] queries = {
             Constants.INIT_EMPLOYEE_TABLE_QUERY,
             Constants.INIT_PROJECT_TABLE_QUERY,
             Constants.INIT_TASK_TABLE_QUERY,
             Constants.INIT_PROJECT_EMPLOYEE_TABLE_QUERY,
             Constants.INIT_BUG_REPORT_TABLE_QUERY,
             Constants.INIT_DOCUMENTATION_TABLE_QUERY,
             Constants.INIT_EVENT_TABLE_QUERY
         };

         try {
             for (String query : queries) {
                 PreparedStatement statement = connection.prepareStatement(query);
                 statement.execute();
             }

             logger.debug("initDatabase[1]: all entities were initialized successfully");
         }
        catch (SQLException exception) {
            logger.error("initDatabase[2]: {}", exception.getMessage());
        }
     }

    /**
     * @param entityName entity name for logging what you save
     * @param methodName method name which runs processNewEntity method
     * @param query query that saves an entity
     * @param fields array of object fields
     * @return true if query was completed successful or false if not
     */
    private boolean processNewEntity(String entityName, String methodName, String query, Object ...fields) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 0;
            for (Object field : fields) {
                if (field instanceof java.util.Date || field instanceof Calendar) {
                    java.sql.Date sqlDate;
                    if (field instanceof java.util.Date)
                        sqlDate = new java.sql.Date(((java.util.Date) field).getTime());
                    else
                        sqlDate = new java.sql.Date(((Calendar)field).getTimeInMillis());
                    statement.setObject(++paramIndex, sqlDate);
                    continue;
                }
                statement.setObject(++paramIndex, field);
            }

            statement.executeUpdate();
            logger.debug("{}[1]: {} was created successfully", methodName, entityName);
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return false;
        }
        return true;
    }

    /** Creates new project
     * @param project - instance of Project
     * @return true if operation was successful else false
     */
    @Override
    public boolean processNewProject(Project project) {
        return processNewEntity(
            project.getClass().getName(),
                "processNewProject",
                Constants.CREATE_PROJECT_QUERY,
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name(),
                new Timestamp(project.getDeadline().getTimeInMillis()),
                project.getManager().getId()
        );
    }

    /**
     * @param employee Employee instance
     * @return true if operation was successful else false
     */
    public boolean processNewEmployee(Employee employee) {
        return processNewEntity(
                employee.getClass().getName(),
                "processNewEmployee",
                Constants.CREATE_EMPLOYEE_QUERY,
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPatronymic(),
                new Date(employee.getBirthday().getTimeInMillis()),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getPosition()
        );
    }


    /**
     * @param task Task instance
     * @return true if operation was successful else false
     */
    @Override
    public boolean processNewTask(Task task) {
        return processNewEntity(
                task.getClass().getName(),
                "processNewTask",
                Constants.CREATE_TASK_QUERY,
                task.getId(),
                task.getProjectId(),
                task.getName(),
                task.getDescription(),
                task.getEmployeeId(),
                task.getEmployeeFullName(),
                task.getComment(),
                task.getPriority().name(),
                task.getTag(),
                task.getStatus().name(),
                task.getDeadline() != null ?
                new Timestamp(task.getDeadline().getTimeInMillis())
                : null,
                new Date(task.getCreatedAt().getTime())
        );
    }


    /**
     * @param bugReport BugReport instance
     * @return true if operation was successful else false
     */
    @Override
    public boolean processNewBugReport(BugReport bugReport) {
        return processNewEntity(
            bugReport.getClass().getName(),
                "processNewBugReport",
                Constants.CREATE_BUG_REPORT_QUERY,
                bugReport.getId(),
                bugReport.getProjectId(),
                bugReport.getStatus().name(),
                bugReport.getPriority().name(),
                bugReport.getName(),
                bugReport.getDescription(),
                bugReport.getEmployeeId(),
                bugReport.getEmployeeFullName(),
                bugReport.getCreatedAt()
        );
    }

    /**
     * @param documentation Documentation instance
     * @return true if operation was successful else false
     */
    @Override
    public boolean processNewDocumentation(Documentation documentation) {
        return processNewEntity(
                documentation.getClass().getName(),
                "processNewDocumentation",
                Constants.CREATE_DOCUMENTATION_QUERY,
                documentation.getId(),
                documentation.getName(),
                documentation.getDescription(),
                documentation.getProjectId(),
                documentation.getEmployeeId(),
                documentation.getEmployeeFullName(),
                documentation.getBody(),
                documentation.getCreatedAt()
        );
    }

    /**
     * @param event Event instance
     * @return true if operation was successful else false
     */
    @Override
    public boolean processNewEvent(Event event) {
        return processNewEntity(
            event.getClass().getName(),
            "processNewEvent",
                Constants.CREATE_EVENT_QUERY,
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getProjectId(),
                event.getEmployeeId(),
                event.getEmployeeFullName(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCreatedAt()
        );
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<BugReport, WorkStatus> trackBugReportStatus() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<Task, WorkStatus> trackTaskStatus() {
        return null;
    }

    /**
     * @param projectId
     * @param checkLaborEfficiency
     * @return
     */
    @Override
    public TrackInfo<String, TrackInfo<Object, Object>> monitorProjectReadiness(
            String projectId, boolean checkLaborEfficiency
    ) {
        return null;
    }

    /**
     * @param projectOd
     * @return
     */
    @Override
    public int calculateProjectReadiness(String projectOd) {
        return 0;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public TrackInfo<Employee, Integer> calculateLaborEfficiency(String projectId) {
        return null;
    }

    private boolean deleteEntity(String entityName, String methodName, String query, Object id) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
            logger.debug("{}[1]: {} with id {} was deleted successfully", methodName, entityName, id);
        }
        catch (SQLException exception) {
            logger.error("deleteProject[2]: {}", exception.getMessage());
            return false;
        }

        return true;
    }


    /**
     * @param projectId
     */
    @Override
    public boolean deleteProject(String projectId) {
        return deleteEntity(
                "project",
                "deleteProject",
                Constants.DELETE_PROJECT_QUERY,
                projectId
        );
    }

    /**
     * @param taskId
     */
    @Override
    public boolean deleteTask(UUID taskId) {
        try (PreparedStatement statement = connection.prepareStatement(Constants.DELETE_PROJECT_QUERY)) {
            statement.setObject(1, taskId);
            logger.debug("deleteTask[1]: Task with id {} was deleted successfully", taskId);
        }
        catch (SQLException exception) {
            logger.error("deleteTask[2]: {}", exception.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @param bugReportId
     */
    @Override
    public boolean deleteBugReport(UUID bugReportId) {
        return true;
    }

    /**
     * @param eventId
     */
    @Override
    public boolean deleteEvent(UUID eventId) {
        return true;
    }

    /**
     * @param docId
     */
    @Override
    public boolean deleteDocumentation(UUID docId) {
        return true;
    }

    /**
     * @param employeeId
     */
    @Override
    public boolean deleteEmployee(UUID employeeId) {
        return true;
    }

    /**
     * @param manager 
     * @param projectId
     */
    @Override
    public void bindProjectManager(Employee manager, String projectId) {

    }

    /**
     * @param executor 
     * @param projectId
     */
    @Override
    public void bindTaskExecutor(Employee executor, String projectId) {

    }

    /**
     * @param id
     * @return
     */
    @Override
    public Project getProjectById(String id) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<Task> getTaskByProjectId(String projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Task getTaskById(UUID taskId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<BugReport> getBugReportsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public BugReport getBugReportById(UUID bugReportId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<Event> getEventsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Event getEventById(UUID eventId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Documentation getDocumentationByProjectId(String projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<Employee> getProjectTeam(String projectId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Employee getEmployee(UUID employeeId) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public ProjectEntity getProjectEntityById(int id) {
        return null;
    }

    /**
     * @param entity
     * @param projectId
     */
    @Override
    public void bindEntityToProject(ProjectEntity entity, String projectId) {

    }
}
