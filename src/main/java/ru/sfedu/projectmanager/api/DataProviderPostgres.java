package ru.sfedu.projectmanager.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.*;
import ru.sfedu.projectmanager.utils.ConfigPropertiesUtil;
import ru.sfedu.projectmanager.utils.Pair;
import ru.sfedu.projectmanager.utils.ResultCode;
import ru.sfedu.projectmanager.utils.ResultSetUtils;

import java.sql.*;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class DataProviderPostgres extends DataProvider {
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
     * @return Result with execution code and message if it fails
     */
    private Result<?> processNewEntity(String entityName, String methodName, String query, Object ...fields) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 0;
            for (Object field : fields) {
                if (field instanceof LocalDateTime) {
                    Timestamp sqlDate;
                    sqlDate = Timestamp.valueOf(((LocalDateTime) field));
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
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
        return new Result<>(ResultCode.SUCCESS);
    }

    /**
     * @param tableName entity table name
     * @param id object id
     * @return Result with execution code and message if it fails
     */
    private Result<?> deleteEntity(String tableName, Object id) throws SQLException {
        String query = String.format(Constants.DELETE_ENTITY_QUERY, tableName);
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(1, id);
        statement.executeUpdate();

        return new Result<>(ResultCode.SUCCESS);
    }


    /**
     *
     * @param entityName name of updatable entity
     * @param methodName method which updates entity
     * @param tableName name of entity table
     * @param columnName column to update
     * @param newValue new value of column
     * @param conditionValue query condition
     * @return Result with execution code and message if it fails
     */
    private Result<?> updateEntityColumn(
            String entityName,
            String methodName,
            String tableName,
            String columnName,
            Object newValue,
            Object conditionValue
    ) throws SQLException {
        int paramIndex = 0;
        String query = String.format(Constants.UPDATE_ENTITY_QUERY, tableName, columnName);

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(++paramIndex, newValue);
        statement.setObject(++paramIndex, conditionValue);
        statement.executeUpdate();

        logger.debug("{}[0]: {} updated successfully", methodName, entityName);
        return new Result<>(ResultCode.SUCCESS);
    }

    /**
     * @param entityName name of updatable entity
     * @param methodName method which updates entity
     * @param query update query
     * @param params entity fields
     * @return Result with execution code and message if it fails
     */
    private Result<?> updateEntity(
            String entityName,
            String methodName,
            String query,
            Object ...params
    ) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        int paramIndex = 0;
        for (Object param : params)
            statement.setObject(++paramIndex, param);
        statement.executeUpdate();

        logger.debug("{}[0]: {} updated successfully", methodName, entityName);
        return new Result<>(ResultCode.SUCCESS);
//        }
//        catch (SQLException exception) {
//            logger.error("{}[1]: {}", methodName, exception.getMessage());
//            return new Result<>(ResultCode.ERROR, exception.getMessage());
//        }
    }

    /**
     *
     * @param entity
     * @param methodName
     * @param queryResult
     * @param changeType
     */
    private void logEntity(Object entity, String methodName, ResultCode queryResult, ChangeType changeType) {
        ActionStatus status = queryResult == ResultCode.SUCCESS ? ActionStatus.SUCCESS : ActionStatus.FAULT;
        HistoryRecord<Object> historyRecord =  new HistoryRecord<>(
                entity,
                methodName,
                status,
                changeType
        );

        logger.debug("logEntity[1]: object {entity} saved to history");
        MongoHistoryProvider.save(historyRecord);

    }


    /** Creates new project
     * @param project - instance of Project
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> processNewProject(Project project) {
        final String methodName = "processNewProject";

        Result<?> result = processNewEntity(
            project.getClass().getName(),
                methodName,
                Constants.CREATE_PROJECT_QUERY,
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name(),
                project.getDeadline() == null ? null :
                        Timestamp.valueOf(project.getDeadline()),
                project.getManager() == null ? null : project.getManager().getId()
        );

        logEntity(
                project,
                methodName,
                result.getCode(),
                ChangeType.CREATE
        );

        return result;
    }

    /**
     * @param employee Employee instance
     * @return Result with execution code and message if it fails
     */
    public Result<?> processNewEmployee(Employee employee) {
        final String methodName = "processNewEmployee";

        Result<?> result = processNewEntity(
                employee.getClass().getName(),
                methodName,
                Constants.CREATE_EMPLOYEE_QUERY,
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPatronymic(),
                Date.valueOf(employee.getBirthday()),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getPosition()
        );

        logEntity(
                employee,
                methodName,
                result.getCode(),
                ChangeType.CREATE
        );

        return result;
    }


    /**
     * @param task Task instance
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> processNewTask(Task task) {
        final String methodName = "processNewTask";

        Result<?> result = processNewEntity(
                task.getClass().getName(),
                methodName,
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
                Timestamp.valueOf(task.getDeadline())
                : null,
                task.getCreatedAt(),
                task.getCompletedAt()
        );
        logEntity(
                task,
                methodName,
                result.getCode(),
                ChangeType.CREATE
        );

        return result;
    }


    /**
     * @param bugReport BugReport instance
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> processNewBugReport(BugReport bugReport) {
        final String methodName = "processNewBugReport";

        Result<?> result = processNewEntity(
            bugReport.getClass().getName(),
                methodName,
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

        logEntity(
                bugReport,
                methodName,
                result.getCode(),
                ChangeType.CREATE
        );
        return result;
    }

    /**
     * @param documentation Documentation instance
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> processNewDocumentation(Documentation documentation) {
        final String methodName = "processNewDocumentation";
        Pair<String[], String[]> documentationBody = splitDocumentationToArrays(documentation.getBody());

        try {
                Result<?> result = processNewEntity(
                        documentation.getClass().getName(),
                        methodName,
                        Constants.CREATE_DOCUMENTATION_QUERY,
                        documentation.getId(),
                        documentation.getName(),
                        documentation.getDescription(),
                        documentation.getProjectId(),
                        documentation.getEmployeeId(),
                        documentation.getEmployeeFullName(),
                        connection.createArrayOf("TEXT", documentationBody.getKey()),
                        connection.createArrayOf("TEXT", documentationBody.getValue()),
                        documentation.getCreatedAt()

                );

                logEntity(
                        documentation,
                        methodName,
                        result.getCode(),
                        ChangeType.CREATE
                );

                return result;
        }
        catch (SQLException exception) {
            logger.error("processNewDocumentation[1]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param docBody
     * @return
     */
    private Pair<String[], String[]> splitDocumentationToArrays(HashMap<String, String> docBody) {
        ArrayList<String> articleTitles = new ArrayList<>();
        ArrayList<String> articles = new ArrayList<>();

        for (Map.Entry<String, String> article : docBody.entrySet()) {
            articleTitles.add(article.getKey());
            articles.add(article.getValue());
        }

        logger.debug("splitDocumentationToArrays[1]: documentation was splitted into arrays:\n{}\n{}", articleTitles, articles);

        return new Pair<>(articleTitles.toArray(new String[0]), articles.toArray(new String[0]));
    }

    /**
     * @param event Event instance
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> processNewEvent(Event event) {
        final String methodName = "processNewEvent";

        Result<?> result = processNewEntity(
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

        logEntity(
                event,
                methodName,
                result.getCode(),
                ChangeType.CREATE
        );
        return result;
    }


    /**
     * @param projectId - id of project you want to delete
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> deleteProject(String projectId) {
        final String methodName = "deleteProject";

        try {
            Result<Project> projectResult = getProjectById(projectId);
            if (projectResult.getCode() == ResultCode.SUCCESS) {
                Result<?> deleteResult = deleteEntity(Constants.PROJECT_TABLE_NAME, projectId);
                logEntity(
                        projectResult.getData(),
                        methodName,
                        deleteResult.getCode(),
                        ChangeType.DELETE
                );
                logger.debug("{}[1]: Project with id {} was deleted successfully", methodName, projectId);
                return deleteResult;
            }
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }

        return new Result<>(null, ResultCode.NOT_FOUND);
    }

    /**
     * @param taskId - id of task you want to delete
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> deleteTask(UUID taskId) {
        final String methodName = "deleteTask";

        try {
            Result<Task> taskResult = getTaskById(taskId);
            if (taskResult.getCode() == ResultCode.SUCCESS) {
                Result<?> result = deleteEntity(Constants.TASKS_TABLE_NAME, taskId);
                logEntity(
                        taskResult.getData(),
                        methodName,
                        result.getCode(),
                        ChangeType.DELETE
                );
                logger.debug("{}[1]: Task with id {} was deleted successfully", methodName, taskId);
                return result;
            }
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
        return new Result<>(null, ResultCode.NOT_FOUND);
    }

    /**
     * @param bugReportId - id of bug report you want to delete
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> deleteBugReport(UUID bugReportId) {
        final String methodName = "deleteBugReport";

        try {
            Result<BugReport> bugReportResult = getBugReportById(bugReportId);
            if (bugReportResult.getCode() == ResultCode.SUCCESS) {
                Result<?> result = deleteEntity(Constants.BUG_REPORTS_TABLE_NAME, bugReportId);
                logEntity(
                        bugReportResult.getData(),
                        methodName,
                        result.getCode(),
                        ChangeType.DELETE
                );
                logger.debug("{}[1]: BugReport with id {} was deleted successfully", methodName, bugReportId);
                return result;
            }
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }

        return new Result<>(null, ResultCode.NOT_FOUND);
    }

    /**
     * @param eventId - id of event you want to delete
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> deleteEvent(UUID eventId) {
        final String methodName = "deleteEvent";

        try {
            Result<Event> eventResult = getEventById(eventId);
            if (eventResult.getCode() == ResultCode.SUCCESS) {
                Result<?> result = deleteEntity(Constants.EVENTS_TABLE_NAME, eventId);
                logEntity(
                        eventResult.getData(),
                        methodName,
                        result.getCode(),
                        ChangeType.DELETE
                );

                logger.debug("{}[1]: Event with id {} was deleted successfully", methodName, eventId);
                return result;
            }

            return new Result<>(null, ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param docId - id of documentation you want to delete
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> deleteDocumentation(UUID docId) {
        final String methodName = "deleteDocumentation";
        try {
            Result<Documentation> documentationResult = getDocumentationById(docId);
            if (documentationResult.getCode() == ResultCode.SUCCESS) {
                Result<?> result = deleteEntity(Constants.DOCUMENTATIONS_TABLE_NAME, docId);
                logEntity(
                        documentationResult,
                        methodName,
                        result.getCode(),
                        ChangeType.DELETE
                );

                logger.debug("{}[1]: BugReport with id {} was deleted successfully", methodName, docId);
                return result;
            }
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }

        return new Result<>(null, ResultCode.NOT_FOUND);
    }

    /**
     * @param employeeId - id of employee you want to delete
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<?> deleteEmployee(UUID employeeId) {
        final String methodName = "deleteEmployee";
        try {
            Result<Employee> employeeResult = getEmployeeById(employeeId);

            if (employeeResult.getCode() == ResultCode.SUCCESS) {
                Result<?> result = deleteEntity(Constants.EMPLOYEES_TABLE_NAME, employeeId);
                logEntity(
                        employeeResult,
                        methodName,
                        result.getCode(),
                        ChangeType.DELETE
                );

                logger.debug("{}[1]: BugReport with id {} was deleted successfully", methodName, employeeId);
                return result;
            }
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception);
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }

        return new Result<>(null, ResultCode.NOT_FOUND);
    }

    /**
     * @param managerId id of the manager who binds to the project
     * @param projectId id of the project to which the manager is attached
     */
    @Override
    public Result<?> bindProjectManager(UUID managerId, String projectId) {
        try {
            return updateEntityColumn(
                "Project",
                    "bindProjectManager",
                    Constants.PROJECT_TABLE_NAME,
                    "manager_id",
                    managerId,
                    projectId
            );
        }
        catch (SQLException exception) {
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param executorId id of the employee that binds to the task
     * @param taskId id of the task to which the employee is attached
     * @param projectId id of the project which have this task
     */
    @Override
    public Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, String projectId) {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE project_id = ?", Constants.EMPLOYEE_PROJECT_TABLE_NAME);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            int rowCount = 0;
            while (resultSet.next()) rowCount = resultSet.getInt("count");

            if (rowCount > 0) {
                return updateEntity(
                    "Task",
                    "bindTaskExecutor",
                    Constants.UPDATE_TASK_EXECUTOR_QUERY,
                    executorId, executorFullName, taskId, projectId

                );

            }
            return new Result<>(null, ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("bindTaskExecutor[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    @Override
    public Result<?> bindEmployeeToProject(UUID employeeId, String projectId) {
        Result<?> createResult = processNewEntity(
            "bindEmployeeToProject",
                "bindEmployeeToProject",
                Constants.CREATE_EMPLOYEE_PROJECT_LINK_QUERY,
                employeeId, projectId
        );

        if (createResult.getCode() == ResultCode.ERROR)
            return new Result<>(
                    null,
                    ResultCode.NOT_FOUND,
                    "Unable to link employee to project"
            );

        return new Result<>(
                ResultCode.SUCCESS,
                String.format(
                        "employee with id %s successfully linked to project{%s}",
                        employeeId.toString(), projectId
                )
        );
    }

    /**
     * @param id id of Project
     * @return Result with Project
     */
    @Override
    public Result<Project> getProjectById(String id) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.PROJECT_TABLE_NAME);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet queryResult = statement.executeQuery();
            Project project = null;
            while (queryResult.next()) project = ResultSetUtils.extractProject(queryResult);

            if (project == null) {
                logger.debug("getProjectById[1]: tasks were not found");
                return new Result<>(null, ResultCode.NOT_FOUND);
            }
            logger.debug("getProjectById[2]: received project {}", project);
            return new Result<>(project, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getProjectById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param projectId id of Project
     * @return Result with Array of tasks
     */
    @Override
    public Result<ArrayList<Task>> getTasksByProjectId(String projectId) {
        String query = String.format(Constants.GET_ENTITY_BY_PROJECT_ID_QUERY, Constants.TASKS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectId);
            ArrayList<Task> tasks = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) tasks.add(ResultSetUtils.extractTask(resultSet));

            if (tasks.isEmpty()) {
                logger.debug("getTasksByProjectId[1]: tasks were not found");
                return new Result<>(tasks, ResultCode.NOT_FOUND);
            }

            logger.debug("getTasksByProjectId[2]: received tasks {}", tasks);
            return new Result<>(tasks, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getTasksByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     *
     * @param employeeId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) {
        try (PreparedStatement statement = connection.prepareStatement(Constants.GET_TASKS_BY_EMPLOYEE_ID_QUERY)) {
            statement.setObject(1, employeeId);
            ArrayList<Task> tasks = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) tasks.add(ResultSetUtils.extractTask(resultSet));
            if (tasks.isEmpty()) {
                logger.debug("getTasksByEmployeeId[1]: tasks were not found");
                return new Result<>(tasks, ResultCode.NOT_FOUND);
            }

            logger.debug("getTasksByEmployeeId[2]: received {}", tasks);
            return new Result<>(tasks, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getTasksByEmployeeId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param taskId id of task you want to get by id
     * @return Result with Task and execution code
     */
    @Override
    public Result<Task> getTaskById(UUID taskId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.TASKS_TABLE_NAME);

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, taskId);
            ResultSet resultSet = statement.executeQuery();
            Task task = null;

            while (resultSet.next()) task = ResultSetUtils.extractTask(resultSet);
            if (task == null) {
                logger.debug("getTaskById[1]: task was not found");
                return new Result<>(null, ResultCode.NOT_FOUND);
            }

            logger.debug("getTaskById[2]: received task {}", task);
            return new Result<>(task, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getTaskById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param projectId id of project where bug reports are loaded from
     * @return Result with ArrayList of ProjectEntity and execution code
     */
    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(String projectId) {
        String query = String.format(Constants.GET_ENTITY_BY_PROJECT_ID_QUERY, Constants.BUG_REPORTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<BugReport> bugReports = new ArrayList<>();

            while (resultSet.next())
                bugReports.add(ResultSetUtils.extractBugReport(resultSet));

            if (bugReports.isEmpty()) {
                logger.debug("getBugReportsByProjectId[1]: bug reports were not found");
                return new Result<>(bugReports, ResultCode.NOT_FOUND);
            }

            logger.debug("getBugReportsByProjectId[2]: received BugReport list {}", bugReports);
            return new Result<>(bugReports, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getBugReportsByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param bugReportId id of BugReport you want to get
     * @return Result with ProjectEntity and execution code
     */
    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.BUG_REPORTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, bugReportId);
            ResultSet resultSet = statement.executeQuery();
            BugReport bugReport = null;
            while (resultSet.next()) bugReport = ResultSetUtils.extractBugReport(resultSet);

            if (bugReport == null) {
                logger.debug("getBugReportById[1]: bug report was not found");
                return new Result<>(null, ResultCode.NOT_FOUND);
            }

            logger.debug("getBugReportById[2]: received {}", bugReport);
            return new Result<>(bugReport, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getBugReportById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param projectId id of the project for which events are selected
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(String projectId) {
        String query = String.format(Constants.GET_ENTITY_BY_PROJECT_ID_QUERY, Constants.EVENTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Event> events = new ArrayList<>();

            while (resultSet.next()) events.add(ResultSetUtils.extractEvent(resultSet));
            if (events.isEmpty()) {
                logger.debug("getEventsByProjectId[1]: events were not found");
                return new Result<>(events, ResultCode.NOT_FOUND);
            }

            logger.debug("getEventsByProjectId[2]: received {}", events);
            return new Result<>(events, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getEventsByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param eventId id of the event
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<Event> getEventById(UUID eventId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.EVENTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, eventId);
            ResultSet resultSet = statement.executeQuery();

            Event event = null;
            while (resultSet.next()) event = ResultSetUtils.extractEvent(resultSet);

            if (event == null) {
                logger.debug("getEventById[1]: event was not found");
                return new Result<>(null, ResultCode.NOT_FOUND);
            }

            logger.debug("getEventById[2]: received {}", event);
            return new Result<>(event, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getEventById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.DOCUMENTATIONS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, docId);
            ResultSet resultSet = statement.executeQuery();
            Documentation documentation = null;

            while (resultSet.next()) documentation = ResultSetUtils.extractDocumentation(resultSet);
            if (documentation == null) {
                logger.debug("getDocumentationById[1]: documentation was not found");
                return new Result<>(null, ResultCode.NOT_FOUND);
            }

            logger.debug("getDocumentationById[2]: received documentation {}", documentation);
            return new Result<>(documentation, ResultCode.SUCCESS);
        }
        catch (SQLException | IllegalArgumentException exception) {
            logger.error("getDocumentationById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param projectId id of the project for which documentation is selected
     * @return Result with execution code and message if it fails
     */
    @Override
    public Result<ArrayList<Documentation>> getDocumentationsByProjectId(String projectId) {
        String query = String.format(Constants.GET_ENTITY_BY_PROJECT_ID_QUERY, Constants.DOCUMENTATIONS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Documentation> documentations = new ArrayList<>();

            while (resultSet.next()) documentations.add(ResultSetUtils.extractDocumentation(resultSet));
            if (documentations.isEmpty()) {
                logger.debug("getDocumentationsByProjectId[1]: documentations were not found");
                return new Result<>(documentations, ResultCode.NOT_FOUND);
            }

            logger.debug("getDocumentationByProjectId[2]: received {}", documentations);
            return new Result<>(documentations, ResultCode.SUCCESS);
        }
        catch (SQLException | IllegalArgumentException exception) {
            logger.error("getDocumentationByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param projectId id of project for which team is selected
     * @return Result with ArrayList of Employee, execution code and message if it fails
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(String projectId) {
        try(PreparedStatement statement = connection.prepareStatement(Constants.GET_PROJECT_TEAM_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Employee> team = new ArrayList<>();

            while (resultSet.next()) team.add(ResultSetUtils.extractEmployee(resultSet));
            if (team.isEmpty()) {
                logger.debug("getProjectTeam[1]: team was not found");
                return new Result<>(team, ResultCode.NOT_FOUND);
            }

            logger.debug("getProjectTeam[2]: received team {}", team);
            return new Result<>(team, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getProjectTeam[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param employeeId id of the employee whose data is being extracted
     * @return Result with Employee, execution code and message if it fails
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.EMPLOYEES_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            Employee employee = null;

            while (resultSet.next()) employee = ResultSetUtils.extractEmployee(resultSet);
            if (employee == null) {
                logger.debug("getEmployeeById[1]: employee was not found");
                return new Result<>(null, ResultCode.NOT_FOUND);
            }

            logger.debug("getEmployee[1]: employee received {}", employee);
            return new Result<>(employee, ResultCode.SUCCESS);
        }
        catch (SQLException exception) {
            logger.error("getEmployee[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<BugReport, String> trackBugReportStatus(String projectId) {
        ArrayList<BugReport> bugReports = getBugReportsByProjectId(projectId).getData();
        if (bugReports != null) {
            HashMap<BugReport, String> trackInfo = new HashMap<>();
            for (BugReport bugReport : bugReports)
                trackInfo.put(bugReport, bugReport.getStatus().name());

            return new TrackInfo<>(trackInfo);
        }
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<Task, String> trackTaskStatus(String projectId) {
        ArrayList<Task> tasks = getTasksByProjectId(projectId).getData();
        if (tasks != null) {
            HashMap<Task, String> trackInfo = new HashMap<>();

            for (Task task : tasks)
                trackInfo.put(task, task.getStatus().name());

            return new TrackInfo<>(trackInfo);

        }
        return null;
    }

    /**
     * @param projectId
     * @param checkLaborEfficiency
     * @return
     */
    @Override
    public TrackInfo<String, ?> monitorProjectCharacteristics(
            String projectId, boolean checkLaborEfficiency, boolean trackBugs
    ) {
        HashMap<String, ?> mainData = new HashMap<>() {{
           put(Constants.TRACK_INFO_KEY_PROJECT_READINESS, calculateProjectReadiness(projectId) );
           put(Constants.TRACK_INFO_KEY_TASK_STATUS, trackTaskStatus(projectId));
        }};

        TrackInfo<String, ?> commonTrackInfo = new TrackInfo<>(mainData);

        if (checkLaborEfficiency)
            commonTrackInfo.addData(
                    Constants.TRACK_INFO_KEY_LABOR_EFFICIENCY,
                    calculateLaborEfficiency(projectId)
            );
        if (trackBugs)
            commonTrackInfo.addData(
                    Constants.TRACK_INFO_KEY_BUG_STATUS,
                    trackBugReportStatus(projectId)
            );

        return commonTrackInfo;
    }

    /**
     * @param projectId id of the project you want to calculate degree of readiness
     * @return float value of readiness in per cent
     */
    @Override
    public float calculateProjectReadiness(String projectId) {
        ArrayList<Task> tasks = getTasksByProjectId(projectId).getData();
        if (!tasks.isEmpty()) {
            int countOfCompletedTasks = 0;
            for (Task task : tasks)
                if (task.getStatus() == WorkStatus.COMPLETED)
                    countOfCompletedTasks++;

            return ((float) countOfCompletedTasks / tasks.size()) * 100.0f;
        }
        return 0;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public TrackInfo<Employee, Float> calculateLaborEfficiency(String projectId) {
        ArrayList<Employee> team = getProjectTeam(projectId).getData();
        HashMap<Employee, Float> trackInfo = new HashMap<>();

        if (!team.isEmpty()) {
            for (Employee employee : team) {
                // получаем список задач сотрудника
                ArrayList<Task> employeeTasks = getTasksByEmployeeId(employee.getId()).getData();
                if (!employeeTasks.isEmpty()) {
                    float averageEffectiveness = checkEmployeeEfficiency(employeeTasks);
                    trackInfo.put(employee, averageEffectiveness);
                }
                else {
                    trackInfo.put(employee, 0f);
                    return new TrackInfo<>(trackInfo);
                }
            }

            return new TrackInfo<>(trackInfo);
        }
        return null;
    }

    /**
     *
     * @param tasks
     * @return
     */
    private float checkEmployeeEfficiency(ArrayList<Task> tasks) {
        // размер списка задач
        int tasksCount = tasks.size();
        // сумма эффективности выполнения проектов в процентах. Далее будет вычисляться средняя эффективность
        int taskEffectivenessSum = 0;
        float averageEffectiveness = 0;

        for (Task task : tasks) {
            LocalDateTime taskDeadline = task.getDeadline();

            if (task.getStatus() == WorkStatus.COMPLETED) {
                // разница в днях между текущей датой и дедлайном
                long timeDifference = Math.abs(Duration.between(taskDeadline, task.getCompletedAt()).toDays());
                if (taskDeadline.isBefore(task.getCompletedAt()))
                    taskEffectivenessSum += (int) (100 - timeDifference);
                else taskEffectivenessSum += (int) (100 + timeDifference);
            }
            else if (task.getStatus() == WorkStatus.IN_PROGRESS) {
                // если задача просрочена - вычисляем эффективность выполнения путем вычитания из 100%
                // количества просроченных дней. Иначе - эффективность выполняющейся задачи равна 0
                if (taskDeadline.isBefore(LocalDateTime.now())) {
                    // разница в днях между текущей датой и дедлайном
                    long timeDifference = Duration.between(taskDeadline, LocalDateTime.now()).toDays();
                    taskEffectivenessSum += (int) (100 - timeDifference);
                }
            }
        }
        return (taskEffectivenessSum * 1.0f) / tasksCount;
    }

}