package ru.sfedu.projectmanager.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.*;
import ru.sfedu.projectmanager.utils.ConfigPropertiesUtil;
import ru.sfedu.projectmanager.utils.Pair;
import ru.sfedu.projectmanager.utils.ResultCode;

import java.sql.*;
import java.sql.Date;
import java.util.*;

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
    private Result<?> processNewEntity(String entityName, String methodName, String query, Object ...fields) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 0;
            for (Object field : fields) {
                if (field instanceof java.util.Date || field instanceof Calendar) {
                    Timestamp sqlDate;
                    if (field instanceof java.util.Date)
                        sqlDate = new Timestamp(((java.util.Date) field).getTime());
                    else
                        sqlDate = new Timestamp(((Calendar)field).getTimeInMillis());
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
        return new Result<>(ResultCode.ACCESS);
    }

    /**
     * @param entityName entity name for logging what you delete
     * @param methodName method name which runs deleteEntity method
     * @param tableName entity table name
     * @param id object id
     * @return true if query was completed successful or false if not
     */
    private Result<?> deleteEntity(String entityName, String methodName, String tableName, Object id) {
        String query = String.format(Constants.DELETE_ENTITY_QUERY, tableName);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
            logger.debug("{}[1]: {} with id {} was deleted successfully", methodName, entityName, id);
        }
        catch (SQLException exception) {
            logger.error("deleteProject[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }

        return new Result<>(ResultCode.ACCESS);
    }

    private Result<?> updateEntity(
            String entityName,
            String methodName,
            String tableName,
            String columnName,
            Object newValue,
            Object conditionValue
    ) {
        String query = String.format(Constants.UPDATE_ENTITY_QUERY, tableName, columnName);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, newValue);
            statement.setObject(2, conditionValue);
            statement.executeUpdate();

            logger.debug("{}[0]: {} updated successfully", methodName, entityName);
            return new Result<>(ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("{}[1]: {}", methodName, exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    private Result<?> updateEntity(
            String entityName,
            String methodName,
            String query,
            Object ...params
    ) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 0;
            for (Object param : params)
                statement.setObject(++paramIndex, param);
            statement.executeUpdate();

            logger.debug("{}[0]: {} updated successfully", methodName, entityName);
            return new Result<>(ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("{}[1]: {}", methodName, exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }


    /** Creates new project
     * @param project - instance of Project
     * @return Result with code execution
     */
    @Override
    public Result<?> processNewProject(Project project) {
        MongoHistoryProvider.save(
                new HistoryRecord<>(
                        project,
                        "processNewProject",
                        ActionStatus.SUCCESS,
                        ChangeType.CREATE
                )
        );

        return processNewEntity(
            project.getClass().getName(),
                "processNewProject",
                Constants.CREATE_PROJECT_QUERY,
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name(),
                project.getDeadline() == null ? null :
                new Timestamp(project.getDeadline().getTimeInMillis()),
                project.getManager() == null ? null : project.getManager().getId()
        );
    }

    /**
     * @param employee Employee instance
     * @return Result with code execution
     */
    public Result<?> processNewEmployee(Employee employee) {
        return processNewEntity(
                employee.getClass().getName(),
                "processNewEmployee",
                Constants.CREATE_EMPLOYEE_QUERY,
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPatronymic(),
                new Date(employee.getBirthday()
                        .getTimeInMillis()),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getPosition()
        );
    }


    /**
     * @param task Task instance
     * @return Result with code execution
     */
    @Override
    public Result<?> processNewTask(Task task) {
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
                task.getCreatedAt()
//                new Date(task.getCreatedAt().getTime())
        );
    }


    /**
     * @param bugReport BugReport instance
     * @return Result with code execution
     */
    @Override
    public Result<?> processNewBugReport(BugReport bugReport) {
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
     * @return Result with code execution
     */
    @Override
    public Result<?> processNewDocumentation(Documentation documentation) {
        try(PreparedStatement statement = connection.prepareStatement(Constants.CREATE_DOCUMENTATION_QUERY)) {
            int paramIndex = 0;
            Pair<String[], String[]> documentationBody = splitDocumentationToArrays(documentation.getBody());

            statement.setObject(++paramIndex, documentation.getId());
            statement.setString(++paramIndex, documentation.getName());
            statement.setString(++paramIndex, documentation.getDescription());
            statement.setString(++paramIndex, documentation.getProjectId());
            statement.setObject(++paramIndex, documentation.getEmployeeId());
            statement.setString(++paramIndex, documentation.getEmployeeFullName());
            statement.setArray(++paramIndex, connection.createArrayOf("TEXT" ,documentationBody.getKey()));
            statement.setArray(++paramIndex, connection.createArrayOf("TEXT", documentationBody.getValue()));
            statement.setDate(++paramIndex, new Date(documentation.getCreatedAt().getTime()));

            statement.executeUpdate();

            return new Result<>(ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("processNewDocumentation[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    private Pair<String[], String[]> splitDocumentationToArrays(HashMap<String, String> docBody) {
        ArrayList<String> articleTitles = new ArrayList<>();
        ArrayList<String> articles = new ArrayList<>();

        for (Map.Entry<String, String> article : docBody.entrySet()) {
            articleTitles.add(article.getKey());
            articles.add(article.getValue());
        }

        return new Pair<>(articleTitles.toArray(new String[0]), articles.toArray(new String[0]));
    }

    private HashMap<String, String> convertDocumentationBodyToHashMap(String[] articleTitles, String[] articles) throws IllegalArgumentException {
        HashMap<String, String> body = new HashMap<>();
        if (articleTitles.length != articles.length)
            throw new IllegalArgumentException("The number of headings and articles does not match");

        for (int i = 0; i < articleTitles.length; i++) {
            body.put(articleTitles[i], articles[i]);
        }

        return body;
    }

    /**
     * @param event Event instance
     * @return Result with code execution
     */
    @Override
    public Result<?> processNewEvent(Event event) {
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
     * @param projectId - id of project you want to delete
     * @return Result with code execution
     */
    @Override
    public Result<?> deleteProject(String projectId) {
        return deleteEntity(
                "project",
                "deleteProject",
                Constants.PROJECT_TABLE_NAME,
                projectId
        );
    }

    /**
     * @param taskId - id of task you want to delete
     * @return Result with code execution
     */
    @Override
    public Result<?> deleteTask(UUID taskId) {
        return deleteEntity(
            "Task",
                "deleteTask",
                Constants.TASKS_TABLE_NAME,
                taskId
        );
    }

    /**
     * @param bugReportId - id of bug report you want to delete
     * @return Result with code execution
     */
    @Override
    public Result<?> deleteBugReport(UUID bugReportId) {
        return deleteEntity(
                "BugReport",
                "deleteBugReport",
                Constants.BUG_REPORTS_TABLE_NAME,
                bugReportId
        );
    }

    /**
     * @param eventId - id of event you want to delete
     * @return Result with code execution
     */
    @Override
    public Result<?> deleteEvent(UUID eventId) {
        return deleteEntity(
            "Event",
            "deleteEvent",
            Constants.EVENTS_TABLE_NAME,
            eventId
        );
    }

    /**
     * @param docId - id of documentation you want to delete
     * @return Result with code execution
     */
    @Override
    public Result<?> deleteDocumentation(UUID docId) {
        return deleteEntity(
            "Documentation",
            "deleteDocumentation",
            Constants.DOCUMENTATIONS_TABLE_NAME,
            docId
        );
    }

    /**
     * @param employeeId - id of employee you want to delete
     * @return Result with code execution
     */
    @Override
    public Result<?> deleteEmployee(UUID employeeId) {
        return deleteEntity(
            "Employee",
                "deleteEmployee",
                Constants.EMPLOYEES_TABLE_NAME,
                employeeId
        );
    }

    /**
     * @param managerId id of the manager who binds to the project
     * @param projectId id of the project to which the manager is attached
     */
    @Override
    public Result<?> bindProjectManager(UUID managerId, String projectId) {
        return updateEntity(
            "Project",
                "bindProjectManager",
                Constants.PROJECT_TABLE_NAME,
                "manager_id",
                managerId,
                projectId
        );
    }

    /**
     * @param executorId id of the employee that binds to the task
     * @param taskId id of the task to which the employee is attached
     * @param projectId id of the project which have this task
     */
    @Override
    public Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, String projectId) {
        try(PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM employee_project WHERE project_id = ?")) {
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
            "",
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
                ResultCode.ACCESS,
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

            while (queryResult.next()) {
                String projectName = queryResult.getString("name");
                String projectDescription = queryResult.getString("description");
                String projectId = queryResult.getString("id");
                Calendar projectDeadline = null;
                WorkStatus projectStatus = WorkStatus.valueOf(queryResult.getString("status"));
                UUID managerId = (UUID) queryResult.getObject("manager_id");
                Employee projectManager = getEmployeeById(managerId).getData();
                ArrayList<Employee> projectTeam = getProjectTeam(projectId).getData();
                ArrayList<ProjectEntity> projectTasks = getTasksByProjectId(projectId).getData();
                ProjectEntity projectDocumentation = getDocumentationByProjectId(projectId).getData();
                ArrayList<ProjectEntity> projectEvents = getEventsByProjectId(projectId).getData();
                ArrayList<ProjectEntity> projectBugReports = getBugReportsByProjectId(projectId).getData();

                Timestamp gotTimestamp = queryResult.getTimestamp("deadline");
                if (gotTimestamp != null) {
                    projectDeadline = new GregorianCalendar();
                    projectDeadline.setTimeInMillis(gotTimestamp.getTime());
                }

                project = new Project(
                        projectName,
                        projectDescription,
                        projectId,
                        projectDeadline,
                        projectStatus,
                        projectManager,
                        projectTeam,
                        projectTasks,
                        projectBugReports,
                        projectEvents,
                        projectDocumentation
                );
            }

            logger.debug("getProjectById[1]: received project {}", project);
            return new Result<>(project, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getProjectById[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param projectId id of Project
     * @return Result with Array of tasks
     */
    @Override
    public Result<ArrayList<ProjectEntity>> getTasksByProjectId(String projectId) {
        try(PreparedStatement statement = connection.prepareStatement(Constants.GET_TASKS_QUERY)) {
            ArrayList<ProjectEntity> tasks = new ArrayList<>();
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tasks.add(extractTasks(resultSet));
            }

            return new Result<>(tasks, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getTasksByProjectId[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param taskId id of task you want to get by id
     * @return Result with Task and execution code
     */
    @Override
    public Result<ProjectEntity> getTaskById(UUID taskId) {
        try(PreparedStatement statement = connection.prepareStatement(Constants.GET_TASK_BY_ID_QUERY)) {
            statement.setObject(1, taskId);
            ResultSet resultSet = statement.executeQuery();
            Task task = null;

            while (resultSet.next())
                task = extractTasks(resultSet);

            logger.debug("getTaskById[1]: received task {}", task);
            return new Result<>(task, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getTaskById[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    private Task extractTasks(ResultSet resultSet) throws SQLException {
        UUID taskId = (UUID) resultSet.getObject("id");
        String taskProjectId = resultSet.getString("project_id");
        String taskName = resultSet.getString("name");
        String taskDescription = resultSet.getString("description");
        UUID taskExecutorId = (UUID) resultSet.getObject("executor_id");
        String taskExecutorFullName = resultSet.getString("executor_full_name");
        String taskComment = resultSet.getString("comment");
        Priority taskPriority = Priority.valueOf(resultSet.getString("priority"));
        String taskTag = resultSet.getString("tag");
        WorkStatus taskStatus = WorkStatus.valueOf(resultSet.getString("status"));

        Calendar taskDeadline = null;
        Timestamp deadline = resultSet.getTimestamp("deadline");
        if (deadline != null) {
            taskDeadline = new GregorianCalendar();
            taskDeadline.setTimeInMillis(deadline.getTime());
        }
        Timestamp date = resultSet.getTimestamp("created_at");
        java.util.Date createdAt = null;
        if (date != null) {
            createdAt = new java.util.Date(date.getTime());
        }

        return new Task(
                taskName,
                taskDescription,
                taskId,
                taskExecutorId,
                taskExecutorFullName,
                taskProjectId,
                taskDeadline,
                taskComment,
                taskPriority,
                taskTag,
                taskStatus,
                createdAt
        );
    }

    /**
     * @param projectId id of project where bug reports are loaded from
     * @return Result with ArrayList of ProjectEntity and execution code
     */
    @Override
    public Result<ArrayList<ProjectEntity>> getBugReportsByProjectId(String projectId) {
        try (PreparedStatement statement = connection.prepareStatement(Constants.GET_BUG_REPORTS_BY_PROJECT_ID_QUERY)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<ProjectEntity> bugReports = new ArrayList<>();

            while (resultSet.next())
                bugReports.add(extractBugReport(resultSet));

            logger.debug("getBugReportsByProjectId[1]: received BugReport list {}", bugReports);
            return new Result<>(bugReports, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getBugReportsByProjectId[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param bugReportId id of BugReport you want to get
     * @return Result with ProjectEntity and execution code
     */
    @Override
    public Result<ProjectEntity> getBugReportById(UUID bugReportId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.BUG_REPORTS_TABLE_NAME);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, bugReportId);
            ResultSet resultSet = statement.executeQuery();
            BugReport bugReport = null;
            while (resultSet.next()) bugReport = extractBugReport(resultSet);

            logger.debug("getBugReportById[1]: received {}", bugReport);
            return new Result<>(bugReport, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getBugReportById[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    private BugReport extractBugReport(ResultSet resultSet) throws SQLException {
        UUID bugReportId = (UUID) resultSet.getObject("id");
        String bugReportProjectId = resultSet.getString("project_id");
        BugStatus bugReportStatus = BugStatus.valueOf(resultSet.getString("status"));
        Priority bugReportPriority = Priority.valueOf(resultSet.getString("priority"));
        String bugReportName = resultSet.getString("name");
        String bugReportDescription = resultSet.getString("description");
        UUID bugReportAuthorId = (UUID) resultSet.getObject("author_id");
        String bugReportAuthorFullName = resultSet.getString("author_full_name");
        Timestamp createdAt = resultSet.getTimestamp("created_at");

        java.util.Date bugReportCreatedAt = null;
        if (createdAt != null) {
            bugReportCreatedAt = new java.util.Date(createdAt.getTime());
        }

        return new BugReport(
                bugReportName,
                bugReportDescription,
                bugReportId,
                bugReportProjectId,
                bugReportAuthorId,
                bugReportAuthorFullName,
                bugReportCreatedAt,
                bugReportPriority,
                bugReportStatus
        );
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<ProjectEntity>> getEventsByProjectId(String projectId) {
        try (PreparedStatement statement = connection.prepareStatement(Constants.GET_EVENTS_BY_PROJECT_ID)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<ProjectEntity> events = new ArrayList<>();

            while (resultSet.next()) events.add(extractEvent(resultSet));

            logger.debug("getEventsByProjectId[1]: received {}", events);
            return new Result<>(events, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getEventsByProjectId[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<ProjectEntity> getEventById(UUID eventId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.EVENTS_TABLE_NAME);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, eventId);
            ResultSet resultSet = statement.executeQuery();
            Event event = null;
            while (resultSet.next()) event = extractEvent(resultSet);

            logger.debug("getEventById[1]: received {}", event);
            return new Result<>(event, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getEventById[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    private Event extractEvent(ResultSet resultSet) throws SQLException {
        UUID eventId = (UUID) resultSet.getObject("id");
        String eventProjectId = resultSet.getString("project_id");
        String eventName = resultSet.getString("name");
        String eventDescription = resultSet.getString("description");
        UUID authorId = (UUID) resultSet.getObject("author_id");
        String authorFullName = resultSet.getString("author_full_name");
        Timestamp startDate = resultSet.getTimestamp("start_date");
        Timestamp endDate = resultSet.getTimestamp("end_date");
        Timestamp createdAt = resultSet.getTimestamp("created_at");

        Calendar eventStartDate = null;
        Calendar eventEndDate = null;
        java.util.Date eventCreatedAt = null;

        if (startDate != null) {
            eventStartDate = new GregorianCalendar();
            eventStartDate.setTimeInMillis(startDate.getTime());
        }
        if (endDate != null) {
            eventEndDate = new GregorianCalendar();
            eventEndDate.setTimeInMillis(endDate.getTime());
        }
        if (createdAt != null)
            eventCreatedAt = new java.util.Date(createdAt.getTime());

        return new Event(
                eventName,
                eventDescription,
                eventId,
                eventProjectId,
                authorId,
                authorFullName,
                eventCreatedAt,
                eventStartDate,
                eventEndDate
        );

    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ProjectEntity> getDocumentationByProjectId(String projectId) {
        try (PreparedStatement statement = connection.prepareStatement(Constants.GET_DOCUMENTATION_BY_PROJECT_ID_QUERY)) {
            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            Documentation documentation = null;
            while (resultSet.next()) {
                UUID documentationId = (UUID) resultSet.getObject("id");
                String documentationProjectId = resultSet.getString("project_id");
                String documentationName = resultSet.getString("name");
                String documentationDescription = resultSet.getString("description");
                UUID documentationAuthorId = (UUID) resultSet.getObject("author_id");
                String documentationAuthorFullName = resultSet.getString("author_full_name");
                String[] documentationArticleTitles = (String[]) resultSet.getArray("article_titles").getArray();
                String[] documentationArticles = (String[]) resultSet.getArray("articles").getArray();
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                java.util.Date documentationCreatedAt = null;

                if (createdAt != null) {
                    documentationCreatedAt = new java.util.Date(createdAt.getTime());
                }

                HashMap<String, String> body = convertDocumentationBodyToHashMap(documentationArticleTitles, documentationArticles);

                documentation = new Documentation(
                        documentationName,
                        documentationDescription,
                        documentationId,
                        documentationProjectId,
                        documentationAuthorId,
                        documentationAuthorFullName,
                        documentationCreatedAt,
                        body
                );
            }

            logger.debug("getDocumentationByProjectId[1]: received {}", documentation);
            return new Result<>(documentation, ResultCode.ACCESS);

        }
        catch (SQLException | IllegalArgumentException exception) {
            logger.error("getDocumentationByProjectId[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR);
        }
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(String projectId) {
        try(PreparedStatement statement = connection.prepareStatement(Constants.GET_PROJECT_TEAM_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Employee> team = new ArrayList<>();
            while (resultSet.next()) {
                team.add(extractEmployee(resultSet));
            }

            return new Result<>(team, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getProjectTeam[2]: {}", exception.getMessage());
            return new Result<>(null, ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        String query = String.format(Constants.GET_ENTITY_BY_ID_QUERY, Constants.EMPLOYEES_TABLE_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            Employee employee = null;

            while (resultSet.next()) {
                employee = extractEmployee(resultSet);
            }

            logger.debug("getEmployee[1]: employee received {}", employee);
            return new Result<>(employee, ResultCode.ACCESS);
        }
        catch (SQLException exception) {
            logger.error("getEmployee[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    private Employee extractEmployee(ResultSet resultSet) throws SQLException {
        Calendar employeeBirthday = null;
        Date birthday = resultSet.getDate("birthday");
        if (birthday != null) {
            employeeBirthday = new GregorianCalendar();
            employeeBirthday.setTimeInMillis(birthday.getTime());
        }

        String employeeFirstName =  resultSet.getString("first_name");
        String employeeLastName = resultSet.getString("last_name");
        String employeePatronymic = resultSet.getString("patronymic");
        String employeeEmail = resultSet.getString("email");
        String employeePhoneNumber = resultSet.getString("phone_number");
        String employeePosition = resultSet.getString("position");
        UUID employeeId = (UUID) resultSet.getObject("id");

        return new Employee(
                employeeFirstName,
                employeeLastName,
                employeePatronymic,
                employeeBirthday,
                employeeEmail,
                employeePhoneNumber,
                employeeId,
                employeePosition
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
}
