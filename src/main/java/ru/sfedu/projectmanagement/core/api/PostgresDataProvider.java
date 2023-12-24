package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Queries;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.ChangeType;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.types.Pair;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.ResultSetUtils;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class PostgresDataProvider extends DataProvider {
    private final Logger logger = LogManager.getLogger(PostgresDataProvider.class);
    private final Environment dbEnvironment;
    private String dbName;

    public PostgresDataProvider() {
        dbEnvironment = Environment.valueOf(ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT));
        // define db name (prod or test)
        setDbName();
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
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.debug("getConnection[4]: successful connection");
        }
        catch (SQLException exception) {
            logger.error("getConnection[5]: database error {}", exception.getMessage());
        }

        return connection;
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
            logger.debug("closeConnection[1]: connection closed");
        }
        catch (SQLException exception) {
            logger.error("closeConnection[2]: {}", exception.getMessage());
        }
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
                logger.error("initDatabaseTables[1]: {}", exception.getMessage());
            }
        });

       closeConnection(currentConnection);
    }

    public String generateSqlQuery(String rawQuery, Object ...fields) {
        List<String> formattedData = Arrays.stream(fields)
                .map(Optional::ofNullable)
                .map(fieldOptional ->
                    fieldOptional.map(field -> {
                        String formatted = "'" + field.toString().strip() + "'";
                        if (formatted.substring(1, formatted.length() - 1).matches(Constants.UUID_REGEX)) formatted += "::uuid";
                        return formatted;
                    }).orElse("NULL")
                ).toList();

        String resultQuery = formattedData.stream().reduce(
                rawQuery, (query, field) -> query.replaceFirst("\\?", field)
        ).strip();

        logger.debug("generateSqlQuery[1]: {}", resultQuery);
        return resultQuery;
    }

    private Result<NoData> checkIfEmployeeBelongsToProject(UUID employeeId, UUID projectId) {
        String query = String.format(
                Queries.CHECK_EMPLOYEE_LINK_EXISTENCE_QUERY,
                Queries.EMPLOYEE_PROJECT_TABLE_NAME, employeeId, projectId
        );

        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        Connection connection = getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            int rowCount = 0;
            while (resultSet.next()) rowCount = resultSet.getInt("count");

            if (rowCount == 0)
                return new Result<>(ResultCode.ERROR, String.format(
                        Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT, employeeId)
                );
            return result;
        }
        catch (SQLException exception) {
            logger.error("checkIfEmployeeBelongsToProject[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
        }
        return result;
    }

    @Override
    public Result<NoData> processNewProject(Project project) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
            Queries.CREATE_PROJECT_QUERY,
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getStatus().name(),
            project.getDeadline() == null ? null :
                    Timestamp.valueOf(project.getDeadline())
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            Result<NoData> initEntitiesResult = initProjectEntities(project);
            if (initEntitiesResult.getCode() != ResultCode.SUCCESS)
                return initEntitiesResult;

            logger.debug("processNewProject[1]: project {} was created successfully", project);
            return result;
        }
        catch (SQLException exception) {
            logger.error("processNewProject[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                project,
                "processNewProject",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }


    @Override
    public Result<NoData> processNewEmployee(Employee employee) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
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

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("processNewEmployee[1]: employee {} was created successfully", employee);
            return result;
        }
        catch (SQLException exception) {
            logger.error("processNewEmployee[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                employee,
                "processNewEmployee",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }


    @Override
    public Result<NoData> processNewTask(Task task) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        Result<NoData> validationResult = checkIfEmployeeBelongsToProject(task.getEmployeeId(), task.getProjectId());

        if (validationResult.getCode() != ResultCode.SUCCESS)
            return validationResult;

        try {
            String query = generateSqlQuery(
                    Queries.CREATE_TASK_QUERY,
                    task.getId(),
                    task.getProjectId(),
                    task.getName(),
                    task.getDescription(),
                    task.getEmployeeId(),
                    task.getEmployeeFullName(),
                    task.getComment(),
                    task.getPriority().name(),
                    connection.createArrayOf("VARCHAR", task.getTags().toArray()),
                    task.getStatus().name(),
                    task.getDeadline() != null ?
                            Timestamp.valueOf(task.getDeadline())
                            : null,
                    task.getCreatedAt(),
                    task.getCompletedAt()
            );

            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();

            logger.debug("processNewTask[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "task", task
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("processNewTask[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                task,
                "processNewTask",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewBugReport(BugReport bugReport) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
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

        Result<NoData> checkConstraintResult = checkIfEmployeeBelongsToProject(
                bugReport.getEmployeeId(), bugReport.getProjectId()
        );

        if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
            return checkConstraintResult;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("processNewBugReport[2]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "bug report", bugReport
            ));
            return result;

        }
        catch (SQLException exception) {
            logger.error("processNewBugReport[1]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                    bugReport,
                    "processNewBugReport",
                    result.getCode(),
                    ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewDocumentation(Documentation documentation) {
        Pair<String[], String[]> documentationBody = splitDocumentationToArrays(documentation.getBody());
        Connection connection = getConnection();
        String query = "";
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);

        Result<NoData> checkConstraintResult = checkIfEmployeeBelongsToProject(
                documentation.getEmployeeId(), documentation.getProjectId()
        );
        if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
            return checkConstraintResult;

        try {
            query = generateSqlQuery(
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
        }
        catch (SQLException exception) {
            logger.error("processNewDocumentation[1]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("processNewDocumentation[2]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "documentation", documentation
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("processNewDocumentation[3]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                documentation,
                "processNewDocumentation",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewEvent(Event event) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
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

        Result<NoData> checkConstraintResult = checkIfEmployeeBelongsToProject(event.getEmployeeId(), event.getProjectId());
        if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
            return checkConstraintResult;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("processNewEvent[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "event", event
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("processNewEvent[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                event,
            "processNewEvent",
                result.getCode(),
                ChangeType.CREATE
            );
        }
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

    @Override
    public Result<NoData> deleteProject(UUID projectId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        Result<Project> project = getProjectById(projectId);

        if (project.getCode() != ResultCode.SUCCESS)
            return new Result<>(ResultCode.NOT_FOUND, String.format(
                    Constants.ENTITY_NOT_FOUND_MESSAGE,
                    "project", projectId
            ));

        String query = generateSqlQuery(
                String.format(Queries.DELETE_ENTITY_QUERY, Queries.PROJECT_TABLE_NAME),
                projectId
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("deleteProject[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "project", project.getData()
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("deleteProject[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                project.getData(),
                "deleteProject",
                result.getCode(),
                ChangeType.DELETE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> deleteTask(UUID taskId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
                String.format(Queries.DELETE_ENTITY_QUERY, Queries.TASKS_TABLE_NAME),
                taskId
        );

        Result<Task> task = getTaskById(taskId);
        if (task.getCode() != ResultCode.SUCCESS)
            return new Result<>(ResultCode.NOT_FOUND, String.format(
                Constants.ENTITY_NOT_FOUND_MESSAGE,
                "task", taskId
            ));

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("deleteTask[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", task
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("deleteTask[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
            return result;
        }
        finally {
            closeConnection(connection);
            logEntity(
                task,
                "deleteTask",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }

    @Override
    public Result<NoData> deleteBugReport(UUID bugReportId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        Result<BugReport> bugReportResult = getBugReportById(bugReportId);
        if (bugReportResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(ResultCode.NOT_FOUND, String.format(
                Constants.ENTITY_NOT_FOUND_MESSAGE,
                "bug report", bugReportId
            ));

        String query = generateSqlQuery(
                String.format(Queries.DELETE_ENTITY_QUERY, Queries.BUG_REPORTS_TABLE_NAME),
                bugReportId
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("deleteBugReport[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "bug report", bugReportResult.getData()
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("deleteBugReport[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
            return result;
        }
        finally {
            closeConnection(connection);
            logEntity(
                bugReportResult.getData(),
                "deleteBugReport",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }

    @Override
    public Result<NoData> deleteEvent(UUID eventId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
            String.format(Queries.DELETE_ENTITY_QUERY, Queries.EVENTS_TABLE_NAME),
            eventId
        );

        Result<Event> eventResult = getEventById(eventId);
        if (eventResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(null, ResultCode.NOT_FOUND);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("deleteEvent[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "event", eventResult.getData()
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("deleteEvent[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
            return result;
        }
        finally {
            closeConnection(connection);
            logEntity(
                eventResult.getData(),
                "deleteEvent",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }


    @Override
    public Result<NoData> deleteDocumentation(UUID docId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
                String.format(Queries.DELETE_ENTITY_QUERY, Queries.DOCUMENTATIONS_TABLE_NAME),
                docId
        );
        Result<Documentation> documentationResult = getDocumentationById(docId);
        if (documentationResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(null, ResultCode.NOT_FOUND);


        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("deleteDocumentation[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "documentation", documentationResult.getData()
            ));
            return result;
        }
        catch (SQLException exception) {
            logger.error("deleteDocumentation[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
            return result;
        }
        finally {
            closeConnection(connection);
            logEntity(
                documentationResult.getData(),
                "deleteDocumentation",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }

    @Override
    public Result<NoData> deleteEmployee(UUID employeeId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
                String.format(Queries.DELETE_ENTITY_QUERY, Queries.EMPLOYEES_TABLE_NAME),
                employeeId
        );

        Result<Employee> employeeResult = getEmployeeById(employeeId);
        if (employeeResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(null, ResultCode.NOT_FOUND);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("deleteEmployee[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "employee", employeeId
            ));

            return result;
        }
        catch (SQLException exception) {
            logger.error("deleteEmployee[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
        finally {
            closeConnection(connection);
            logEntity(
                    employeeResult,
                    "deleteEmployee",
                    result.getCode(),
                    ChangeType.DELETE
            );
        }
    }

    @Override
    public Result<NoData> bindProjectManager(UUID managerId, UUID projectId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
                String.format(
                        Queries.UPDATE_ENTITY,
                        Queries.PROJECT_TABLE_NAME,
                        "manager_id = ?"
                ),
                managerId,
                projectId
        );

        Result<NoData> validationResult = checkIfEmployeeBelongsToProject(managerId, projectId);
        if (validationResult.getCode() != ResultCode.SUCCESS)
            return validationResult;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            Result<List<Employee>> teamResult = getProjectTeam(projectId);
            if (teamResult.getCode() != ResultCode.SUCCESS && teamResult.getData().isEmpty())
                return new Result<>(ResultCode.NOT_FOUND);

            List<Employee> team = teamResult.getData();
            if (team.stream().anyMatch(employee -> !employee.getId().equals(managerId)))
                return new Result<>(ResultCode.ERROR, String.format("Employee with id %s doesn't belong to the project", managerId));

            logger.debug("bindProjectManager[1]: employee[{}] became manager of the project[{}]", managerId, projectId);
            return result;
        }
        catch (SQLException exception) {
            logger.error("bindProjectManager[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
            return result;
        }
        finally {
            closeConnection(connection);
            logEntity(
                getProjectById(projectId).getData(),
                "bindProjectManager",
                    result.getCode(),
                    ChangeType.UPDATE
            );
        }
    }

    @Override
    public Result<NoData> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        Connection connection = getConnection();
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        String query = generateSqlQuery(
                Queries.CREATE_EMPLOYEE_PROJECT_LINK_QUERY,
                employeeId, projectId
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            logger.debug("bindEmployeeToProject[1]: employee[{}] was attached to the project[{}] successfully", employeeId, projectId);
            return result;

        }
        catch (SQLException exception) {
            logger.error("bindEmployeeToProject[1]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            closeConnection(connection);
        }
        return result;
    }

    @Override
    public Result<Project> getProjectById(UUID id) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.PROJECT_TABLE_NAME);
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet queryResult = statement.executeQuery();
            Project project = null;
            while (queryResult.next())
                project = ResultSetUtils.extractProject(queryResult, this);

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
        finally {
            closeConnection(connection);
        }
    }


    @Override
    public Result<List<Task>> getTasksByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.TASKS_TABLE_NAME);
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            List<Task> tasks = new ArrayList<>();
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
        finally {
            closeConnection(connection);
        }
    }


    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) {
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<ArrayList<Task>> getTasksByTags(ArrayList<String> tags, UUID projectId) {
        List<Task> tasks = getTasksByProjectId(projectId).getData();
        ArrayList<Task> result = tasks.stream()
                .filter(task -> new HashSet<>(task.getTags()).containsAll(tags))
                .collect(Collectors.toCollection(ArrayList::new));

        return Optional.of(result)
                .filter(r -> !r.isEmpty())
                .map(r -> new Result<>(r, ResultCode.SUCCESS))
                .orElse(new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND));
    }


    @Override
    public Result<Task> getTaskById(UUID taskId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.TASKS_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.BUG_REPORTS_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }


    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.BUG_REPORTS_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.EVENTS_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<Event> getEventById(UUID eventId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.EVENTS_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.DOCUMENTATIONS_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<List<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        String query = String.format(Queries.GET_ENTITY_BY_PROJECT_ID_QUERY, Queries.DOCUMENTATIONS_TABLE_NAME);
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            List<Documentation> documentations = new ArrayList<>();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<List<Employee>> getProjectTeam(UUID projectId) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(Queries.GET_PROJECT_TEAM_QUERY)) {
            statement.setObject(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            List<Employee> team = new ArrayList<>();

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
        finally {
            closeConnection(connection);
        }
    }

    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        String query = String.format(Queries.GET_ENTITY_BY_ID_QUERY, Queries.EMPLOYEES_TABLE_NAME);
        Connection connection = getConnection();

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
        finally {
            closeConnection(connection);
        }
    }
}