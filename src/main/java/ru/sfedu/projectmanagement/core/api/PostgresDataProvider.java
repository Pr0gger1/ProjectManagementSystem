package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Queries;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.ChangeType;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.types.Pair;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.ResultSetUtils;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PostgresDataProvider extends DataProvider {
    private final Logger logger = LogManager.getLogger(PostgresDataProvider.class);
    private Connection connection = null;
    private final Environment dbEnvironment;
    private String dbName;

    public PostgresDataProvider() {
        dbEnvironment = Environment.valueOf(ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT));
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

    /**
     * prepares tables during initialization of postgres data provider
     */
    private void initDatabaseTables() {
        ArrayList<String> queries = new ArrayList<>(List.of(
                Queries.INIT_EMPLOYEE_TABLE_QUERY,
                Queries.INIT_PROJECT_TABLE_QUERY,
                Queries.INIT_TASK_TABLE_QUERY,
                Queries.INIT_PROJECT_EMPLOYEE_TABLE_QUERY,
                Queries.INIT_BUG_REPORT_TABLE_QUERY,
                Queries.INIT_DOCUMENTATION_TABLE_QUERY,
                Queries.INIT_EVENT_TABLE_QUERY
        ));

        Connection currentConnection = getConnection();
        queries.forEach(query -> {
            try {
                PreparedStatement statement = currentConnection.prepareStatement(query);
                statement.execute();
            }
            catch (SQLException exception) {
                logger.error("initDatabase[2]: {}", exception.getMessage());

            }
            finally {
                try {
                    currentConnection.close();
                } catch (SQLException e) {
                    logger.debug("connection close error {}", e.getMessage());
                }
            }
        });
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
        String query = String.format(Queries.DELETE_ENTITY_QUERY, tableName);
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
        String query = String.format(Queries.UPDATE_COLUMN_ENTITY_QUERY, tableName, columnName);

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(++paramIndex, newValue);
        statement.setObject(++paramIndex, conditionValue);
        statement.executeUpdate();

        logger.debug("{}[0]: {} updated successfully", methodName, entityName);
        return new Result<>(ResultCode.SUCCESS);
    }



    @Override
    public Result<?> processNewProject(Project project) {
        final String methodName = "processNewProject";
        if (project.getManager() != null && getEmployeeById(project.getManagerId()).getCode() == ResultCode.NOT_FOUND) {
            processNewEmployee(project.getManager());
        }
        Result<?> result = processNewEntity(
                project.getClass().getName(),
                methodName,
                Queries.CREATE_PROJECT_QUERY,
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
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewEmployee(Employee)}
     */
    @Override
    public Result<?> processNewEmployee(Employee employee) {
        final String methodName = "processNewEmployee";

        Result<?> result = processNewEntity(
                employee.getClass().getName(),
                methodName,
                Queries.CREATE_EMPLOYEE_QUERY,
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
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewTask(Task)}
     */
    @Override
    public Result<?> processNewTask(Task task) {
        final String methodName = "processNewTask";

        try {
            Result<?> result = processNewEntity(
                    task.getClass().getName(),
                    methodName,
                    Queries.CREATE_TASK_QUERY,
                    task.getId(),
                    task.getProjectId(),
                    task.getName(),
                    task.getDescription(),
                    task.getEmployeeId(),
                    task.getEmployeeFullName(),
                    task.getComment(),
                    task.getPriority().name(),
                    connection.createArrayOf("VARCHAR",task.getTags().toArray()),
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
        catch (SQLException exception) {
            logger.error("processNewTask[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewBugReport(BugReport)}
     */
    @Override
    public Result<?> processNewBugReport(BugReport bugReport) {
        final String methodName = "processNewBugReport";

        Result<?> result = processNewEntity(
                bugReport.getClass().getName(),
                methodName,
                Queries.CREATE_BUG_REPORT_QUERY,
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
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewDocumentation(Documentation)}
     */
    @Override
    public Result<?> processNewDocumentation(Documentation documentation) {
        final String methodName = "processNewDocumentation";
        Pair<String[], String[]> documentationBody = splitDocumentationToArrays(documentation.getBody());

        try {
            Result<?> result = processNewEntity(
                    documentation.getClass().getName(),
                    methodName,
                    Queries.CREATE_DOCUMENTATION_QUERY,
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
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewEvent(Event)}
     */
    @Override
    public Result<?> processNewEvent(Event event) {
        final String methodName = "processNewEvent";

        Result<?> result = processNewEntity(
                event.getClass().getName(),
                "processNewEvent",
                Queries.CREATE_EVENT_QUERY,
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
     * @param docBody HashMap with article titles and article bodies
     * @return Pair of two string arrays
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
     * @param query update query
     * @param params entity fields
     * @return Result with execution code and message if it fails
     */
    private int updateEntity(
            String query,
            Object ...params
    ) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        int paramIndex = 0;
        for (Object param : params) {
            if (param instanceof Priority || param instanceof WorkStatus || param instanceof BugStatus)
                statement.setObject(++paramIndex, param, Types.VARCHAR);
            else statement.setObject(++paramIndex, param);
        }
        statement.executeUpdate();

        return statement.getUpdateCount();
    }

//    public Result<?> updateProject(Project project) {
//        try {
//            int updateResult = updateEntity(
//                    Constants.UPDATE_PROJECT_QUERY,
//                    project.getName(),
//                    project.getDescription(),
//                    project.getStatus(),
//                    project.getDeadline(),
//                    project.getManager() == null ?
//                            null : project.getManager().getId(),
//                    project.getId()
//            );
//
//            ArrayList<ProjectEntity> tasks = project.getTasks();
//            ArrayList<ProjectEntity> bugReports = project.getBugReports();
//            ArrayList<ProjectEntity> events = project.getEvents();
//            ArrayList<ProjectEntity> documentations = project.getDocumentations();
//            ArrayList<Employee> team = project.getTeam();
//
//            for (ProjectEntity task : tasks) {
//                Result<?> result = updateTask((Task) task);
//                if (result.getCode() == ResultCode.ERROR || result.getCode() == ResultCode.NOT_FOUND)
//                    return new Result<>(null, ResultCode.ERROR, result.getMessage());
//            }
//
//            for (ProjectEntity bugReport : bugReports) {
//                Result<?> result = updateBugReport((BugReport) bugReport);
//                if (result.getCode() == ResultCode.ERROR || result.getCode() == ResultCode.NOT_FOUND)
//                    return new Result<>(null, ResultCode.ERROR, result.getMessage());
//            }
//
//            for (ProjectEntity event : events) {
//                Result<?> result = updateEvent((Event) event);
//                if (result.getCode() == ResultCode.ERROR || result.getCode() == ResultCode.NOT_FOUND)
//                    return new Result<>(null, ResultCode.ERROR, result.getMessage());
//            }
//
//            for (ProjectEntity doc : documentations) {
//                Result<?> result = updateDocumentation((Documentation) doc);
//                if (result.getCode() == ResultCode.ERROR || result.getCode() == ResultCode.NOT_FOUND)
//                    return new Result<>(null, ResultCode.ERROR, result.getMessage());
//            }
//
//            for (Employee employee : team) {
//                Result<?> result = updateEmployee(employee);
//                if (result.getCode() == ResultCode.ERROR || result.getCode() == ResultCode.NOT_FOUND)
//                    return new Result<>(null, ResultCode.ERROR, result.getMessage());
//            }
//
//            if (updateResult > 0) {
//                logEntity(project, "updateProject", ResultCode.SUCCESS, ChangeType.UPDATE);
//                return new Result<>(ResultCode.SUCCESS);
//            }
//            return new Result<>(ResultCode.NOT_FOUND);
//        }
//        catch (SQLException exception) {
//            logger.error("updateProject[2]: {}", exception.getMessage());
//            return new Result<>(ResultCode.ERROR, exception.getMessage());
//        }
//    }

    public Result<?> updateEmployee(Employee employee) {
        try {
            int result = updateEntity(
                    Queries.UPDATE_EMPLOYEE_QUERY,
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getPatronymic(),
                    employee.getBirthday(),
                    employee.getEmail(),
                    employee.getPhoneNumber(),
                    employee.getPosition(),
                    employee.getId()
            );

            if (result > 0) {
                logEntity(employee, "updateEmployee", ResultCode.SUCCESS, ChangeType.UPDATE);
                return new Result<>(ResultCode.SUCCESS);
            }

            return new Result<>(ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("updateEmployee[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    public Result<?> updateTask(Task task) {
        try {
            int result = updateEntity(
                    Queries.UPDATE_TASK_QUERY,
                    task.getProjectId(),
                    task.getName(),
                    task.getDescription(),
                    task.getEmployeeId(),
                    task.getEmployeeFullName(),
                    task.getComment(),
                    task.getPriority(),
                    connection.createArrayOf("VARCHAR", task.getTags().toArray()),
                    task.getStatus(),
                    task.getDeadline(),
                    task.getCreatedAt(),
                    task.getCompletedAt(),
                    task.getId()
            );

            if (result > 0) {
                logEntity(task, "updateTask", ResultCode.SUCCESS, ChangeType.UPDATE);
                return new Result<>(ResultCode.SUCCESS);
            }

            return new Result<>(ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("updateTask[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    public Result<?> updateBugReport(BugReport bugReport) {
        try {
            int result = updateEntity(
                    Queries.UPDATE_BUG_REPORT_QUERY,
                    bugReport.getProjectId(),
                    bugReport.getStatus(),
                    bugReport.getPriority(),
                    bugReport.getName(),
                    bugReport.getDescription(),
                    bugReport.getEmployeeId(),
                    bugReport.getEmployeeFullName(),
                    bugReport.getCreatedAt(),
                    bugReport.getId()
            );

            if (result > 0) {
                logEntity(bugReport, "updateBugReport", ResultCode.SUCCESS, ChangeType.UPDATE);
                return new Result<>(ResultCode.SUCCESS);
            }

            return new Result<>(ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("updateBugReport[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    public Result<?> updateEvent(Event event) {
        try {
            int result = updateEntity(
                    Queries.UPDATE_EVENT_QUERY,
                    event.getName(),
                    event.getDescription(),
                    event.getProjectId(),
                    event.getEmployeeId(),
                    event.getEmployeeFullName(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getCreatedAt(),
                    event.getId()
            );

            if (result > 0) {
                logEntity(event, "updateEvent", ResultCode.SUCCESS, ChangeType.UPDATE);
                return new Result<>(ResultCode.SUCCESS);
            }

            return new Result<>(ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("updateEvent[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    public Result<?> updateDocumentation(Documentation documentation) {
        Pair<String[], String[]> documentationBody = splitDocumentationToArrays(documentation.getBody());
        try {
            int result = updateEntity(
                    Queries.UPDATE_DOCUMENTATION_QUERY,
                    documentation.getProjectId(),
                    documentation.getName(),
                    documentation.getDescription(),
                    documentation.getEmployeeId(),
                    documentation.getEmployeeFullName(),
                    documentationBody.getKey(),
                    documentationBody.getValue(),
                    documentation.getCreatedAt(),
                    documentation.getId()
            );

            if (result > 0) {
                logEntity(documentation, "updateBugReport", ResultCode.SUCCESS, ChangeType.UPDATE);
                return new Result<>(ResultCode.SUCCESS);
            }

            return new Result<>(null, ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("updateDocumentation[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteProject(UUID)}
     */
    @Override
    public Result<?> deleteProject(UUID projectId) {
        final String methodName = "deleteProject";

        try {
            Result<Project> projectResult = getProjectById(projectId);
            if (projectResult.getCode() == ResultCode.SUCCESS) {
                Result<?> deleteResult = deleteEntity(Queries.PROJECT_TABLE_NAME, projectId);
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
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteTask(UUID)}
     */
    @Override
    public Result<?> deleteTask(UUID taskId) {
        final String methodName = "deleteTask";

        try {
            Result<Task> taskResult = getTaskById(taskId);
            if (taskResult.getCode() != ResultCode.SUCCESS)
                return new Result<>(null, ResultCode.NOT_FOUND);

            Result<?> result = deleteEntity(Queries.TASKS_TABLE_NAME, taskId);
            logEntity(
                    taskResult.getData(),
                    methodName,
                    result.getCode(),
                    ChangeType.DELETE
            );
            logger.debug("{}[1]: Task with id {} was deleted successfully", methodName, taskId);
            return result;
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteBugReport(UUID)}
     */
    @Override
    public Result<?> deleteBugReport(UUID bugReportId) {
        final String methodName = "deleteBugReport";

        try {
            Result<BugReport> bugReportResult = getBugReportById(bugReportId);
            if (bugReportResult.getCode() != ResultCode.SUCCESS)
                return new Result<>(null, ResultCode.NOT_FOUND);

            Result<?> result = deleteEntity(Queries.BUG_REPORTS_TABLE_NAME, bugReportId);
            logEntity(
                    bugReportResult.getData(),
                    methodName,
                    result.getCode(),
                    ChangeType.DELETE
            );
            logger.debug("{}[1]: BugReport with id {} was deleted successfully", methodName, bugReportId);
            return result;
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }

    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteEvent(UUID)}
     */
    @Override
    public Result<?> deleteEvent(UUID eventId) {
        final String methodName = "deleteEvent";

        try {
            Result<Event> eventResult = getEventById(eventId);
            if (eventResult.getCode() != ResultCode.SUCCESS)
                return new Result<>(null, ResultCode.NOT_FOUND);

            Result<?> result = deleteEntity(Queries.EVENTS_TABLE_NAME, eventId);
            logEntity(
                    eventResult.getData(),
                    methodName,
                    result.getCode(),
                    ChangeType.DELETE
            );

            logger.debug("{}[1]: Event with id {} was deleted successfully", methodName, eventId);
            return result;

        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteDocumentation(UUID)}
     */
    @Override
    public Result<?> deleteDocumentation(UUID docId) {
        final String methodName = "deleteDocumentation";
        try {
            Result<Documentation> documentationResult = getDocumentationById(docId);
            if (documentationResult.getCode() != ResultCode.SUCCESS)
                return new Result<>(null, ResultCode.NOT_FOUND);

            Result<?> result = deleteEntity(Queries.DOCUMENTATIONS_TABLE_NAME, docId);
            logEntity(
                    documentationResult,
                    methodName,
                    result.getCode(),
                    ChangeType.DELETE
            );

            logger.debug("{}[1]: BugReport with id {} was deleted successfully", methodName, docId);
            return result;
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }

    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteEmployee(UUID)}
     */
    @Override
    public Result<?> deleteEmployee(UUID employeeId) {
        final String methodName = "deleteEmployee";
        try {
            Result<Employee> employeeResult = getEmployeeById(employeeId);

            if (employeeResult.getCode() != ResultCode.SUCCESS)
                return new Result<>(null, ResultCode.NOT_FOUND);

            Result<?> result = deleteEntity(Queries.EMPLOYEES_TABLE_NAME, employeeId);
            logEntity(
                    employeeResult,
                    methodName,
                    result.getCode(),
                    ChangeType.DELETE
            );

            logger.debug("{}[1]: BugReport with id {} was deleted successfully", methodName, employeeId);
            return result;
        }
        catch (SQLException exception) {
            logger.error("{}[2]: {}", methodName, exception);
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#bindProjectManager(UUID, UUID)}
     */
    @Override
    public Result<?> bindProjectManager(UUID managerId, UUID projectId) {
        try {
            Result<ArrayList<Employee>> teamResult = getProjectTeam(projectId);
            if (teamResult.getCode() != ResultCode.SUCCESS && teamResult.getData().isEmpty())
                return new Result<>(ResultCode.NOT_FOUND);

            ArrayList<Employee> team = teamResult.getData();
            if (team.stream().anyMatch(employee -> !employee.getId().equals(managerId)))
                return new Result<>(ResultCode.ERROR, String.format("Employee with id %s doesn't belong to the project", managerId));

            return updateEntityColumn(
                        "Project",
                        "bindProjectManager",
                        Queries.PROJECT_TABLE_NAME,
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
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#bindTaskExecutor(UUID, String, UUID, UUID)}
     */
    @Override
    public Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, UUID projectId) {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE project_id = ?", Queries.EMPLOYEE_PROJECT_TABLE_NAME);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            int rowCount = 0;
            while (resultSet.next()) rowCount = resultSet.getInt("count");

            if (rowCount > 0) {
                int result = updateEntity(
                        Queries.UPDATE_TASK_EXECUTOR_QUERY,
                        executorId, executorFullName, taskId, projectId
                );

                Result<Task> updatedTask = getTaskById(taskId);
                if (updatedTask.getCode() == ResultCode.SUCCESS) {
                    logEntity(
                            updatedTask.getData(),
                            "bindTaskExecutor",
                            updatedTask.getCode(),
                            ChangeType.UPDATE
                    );
                    if (result > 0) {
                        return new Result<>(ResultCode.SUCCESS);
                    }
                }
            }

            return new Result<>(ResultCode.NOT_FOUND);
        }
        catch (SQLException exception) {
            logger.error("bindTaskExecutor[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#bindEmployeeToProject(UUID, UUID)}
     */
    @Override
    public Result<?> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        Result<?> createResult = processNewEntity(
                "bindEmployeeToProject",
                "bindEmployeeToProject",
                Queries.CREATE_EMPLOYEE_PROJECT_LINK_QUERY,
                employeeId, projectId
        );

        if (createResult.getCode() == ResultCode.ERROR)
            return new Result<>(null, ResultCode.NOT_FOUND, "Unable to link employee to project");

        return new Result<>(
                ResultCode.SUCCESS,
                String.format(
                        "employee with id %s successfully linked to project{%s}",
                        employeeId.toString(), projectId
                )
        );
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getProjectById(UUID)}
     */
    @Override
    public Result<Project> getProjectById(UUID id) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.PROJECT_TABLE_NAME);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet queryResult = statement.executeQuery();
            Project project = null;
            while (queryResult.next()) project = ResultSetUtils.extractProject(queryResult, this);

            return Optional.ofNullable(project)
                .map(p -> {
                    logger.debug("getProjectById[2]: received project {}", p);
                    return new Result<>(p, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getProjectById[1]: tasks were not found");
                    return new Result<>(ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getProjectById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTasksByProjectId(UUID)}
     */
    @Override
    public Result<ArrayList<Task>> getTasksByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.TASKS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            ArrayList<Task> tasks = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) tasks.add(ResultSetUtils.extractTask(resultSet));
            return Optional.of(tasks)
                .filter(t -> !t.isEmpty())
                .map(t -> {
                    logger.debug("getTasksByProjectId[1]: received tasks {}", t);
                    return new Result<>(t, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getTasksByProjectId[2]: tasks were not found");
                    return new Result<>(tasks, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getTasksByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTasksByEmployeeId(UUID)}
     */
    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) {
        try (PreparedStatement statement = connection.prepareStatement(Queries.GET_TASKS_BY_EMPLOYEE_ID_QUERY)) {
            statement.setObject(1, employeeId);
            ArrayList<Task> tasks = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) tasks.add(ResultSetUtils.extractTask(resultSet));

            return Optional.of(tasks)
                .filter(t -> !t.isEmpty())
                .map(t -> {
                    logger.debug("getTasksByEmployeeId[1]: received {}", t);
                    return new Result<>(t, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getTasksByEmployeeId[2]: tasks were not found");
                    return new Result<>(tasks, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getTasksByEmployeeId[3]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTasksByTags(ArrayList, UUID)}
     */
    @Override
    public Result<ArrayList<Task>> getTasksByTags(ArrayList<String> tags, UUID projectId) {
        ArrayList<Task> tasks = getTasksByProjectId(projectId).getData();
        ArrayList<Task> result = tasks.stream()
                .filter(task -> task.getTags().containsAll(tags))
                .collect(Collectors.toCollection(ArrayList::new));

        return Optional.of(result)
                .filter(r -> !r.isEmpty())
                .map(r -> new Result<>(r, ResultCode.SUCCESS))
                .orElse(new Result<>(ResultCode.NOT_FOUND));
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTaskById(UUID)}
     */
    @Override
    public Result<Task> getTaskById(UUID taskId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.TASKS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, taskId);
            ResultSet resultSet = statement.executeQuery();
            Task task = null;

            while (resultSet.next()) task = ResultSetUtils.extractTask(resultSet);

            logger.debug("getTaskById[1]: received task {}", task);
            return Optional.ofNullable(task)
                        .map(t -> new Result<>(t, ResultCode.SUCCESS))
                        .orElse(new Result<>(ResultCode.NOT_FOUND));
        }
        catch (SQLException exception) {
            logger.error("getTaskById[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getBugReportsByProjectId(UUID)}
     */
    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.BUG_REPORTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<BugReport> bugReports = new ArrayList<>();

            while (resultSet.next())
                bugReports.add(ResultSetUtils.extractBugReport(resultSet));

            return Optional.of(bugReports)
                .filter(bg -> !bg.isEmpty())
                .map(bg -> {
                    logger.debug("getBugReportsByProjectId[1]: received BugReport list {}", bugReports);
                    return new Result<>(bg, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getBugReportsByProjectId[2]: bug reports were not found");
                    return new Result<>(bugReports, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getBugReportsByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getBugReportById(UUID)}
     */
    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.BUG_REPORTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, bugReportId);
            ResultSet resultSet = statement.executeQuery();
            BugReport bugReport = null;
            while (resultSet.next()) bugReport = ResultSetUtils.extractBugReport(resultSet);

            return Optional.ofNullable(bugReport)
                .map(bg -> {
                    logger.debug("getBugReportById[1]: received {}", bg);
                    return new Result<>(bg, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getBugReportById[2]: bug report was not found");
                    return new Result<>(null, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getBugReportById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getEventsByProjectId(UUID)}
     */
    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.EVENTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Event> events = new ArrayList<>();

            while (resultSet.next()) events.add(ResultSetUtils.extractEvent(resultSet));

            return Optional.of(events)
                .filter(e -> !e.isEmpty())
                .map(e -> {
                    logger.debug("getEventsByProjectId[1]: received {}", events);
                    return new Result<>(events, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getEventsByProjectId[2]: events were not found");
                    return new Result<>(events, ResultCode.NOT_FOUND);

                });

        }
        catch (SQLException exception) {
            logger.error("getEventsByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getEventById(UUID)}
     */
    @Override
    public Result<Event> getEventById(UUID eventId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.EVENTS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, eventId);
            ResultSet resultSet = statement.executeQuery();

            Event event = null;
            while (resultSet.next()) event = ResultSetUtils.extractEvent(resultSet);

            return Optional.ofNullable(event)
                .map(e -> {
                    logger.debug("getEventById[1]: received {}", e);
                    return new Result<>(e, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getEventById[2]: event was not found");
                    return new Result<>(null, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getEventById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }


    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getDocumentationById(UUID)}
     */
    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.DOCUMENTATIONS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, docId);
            ResultSet resultSet = statement.executeQuery();
            Documentation documentation = null;

            while (resultSet.next()) documentation = ResultSetUtils.extractDocumentation(resultSet);

            return Optional.ofNullable(documentation)
                .map(doc -> {
                    logger.debug("getDocumentationById[1]: received documentation {}", doc);
                    return new Result<>(doc, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getDocumentationById[2]: documentation was not found");
                    return new Result<>(null, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException | IllegalArgumentException exception) {
            logger.error("getDocumentationById[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getDocumentationsByProjectId(UUID)}
     */
    @Override
    public Result<ArrayList<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.DOCUMENTATIONS_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Documentation> documentations = new ArrayList<>();

            while (resultSet.next()) documentations.add(ResultSetUtils.extractDocumentation(resultSet));

            return Optional.of(documentations)
                .filter(docs -> !docs.isEmpty())
                .map(docs -> {
                    logger.debug("getDocumentationByProjectId[1]: received {}", documentations);
                    return new Result<>(documentations, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getDocumentationsByProjectId[2]: documentations were not found");
                    return new Result<>(documentations, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException | IllegalArgumentException exception) {
            logger.error("getDocumentationByProjectId[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getProjectTeam(UUID)}
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(UUID projectId) {
        try(PreparedStatement statement = connection.prepareStatement(Queries.GET_PROJECT_TEAM_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Employee> team = new ArrayList<>();

            while (resultSet.next()) team.add(ResultSetUtils.extractEmployee(resultSet));
            return Optional.of(team)
                .filter(t -> !t.isEmpty())
                .map(t -> {
                    logger.debug("getProjectTeam[1]: received team {}", team);
                    return new Result<>(team, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getProjectTeam[2]: team was not found");
                    return new Result<>(team, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getProjectTeam[3]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getEmployeeById(UUID)}
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.EMPLOYEES_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            Employee employee = null;

            while (resultSet.next())
                employee = ResultSetUtils.extractEmployee(resultSet);

            return Optional.ofNullable(employee)
                .map(e -> {
                    logger.debug("getEmployee[1]: employee received {}", e);
                    return new Result<>(e, ResultCode.SUCCESS);
                })
                .orElseGet(() -> {
                    logger.debug("getEmployeeById[2]: employee was not found");
                    return new Result<>(null, ResultCode.NOT_FOUND);
                });
        }
        catch (SQLException exception) {
            logger.error("getEmployee[3]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }
}