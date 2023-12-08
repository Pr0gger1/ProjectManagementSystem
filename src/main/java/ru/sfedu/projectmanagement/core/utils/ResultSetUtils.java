package ru.sfedu.projectmanagement.core.utils;

import ru.sfedu.projectmanagement.core.api.DataProvider;
import ru.sfedu.projectmanagement.core.api.PostgresDataProvider;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class ResultSetUtils {
    private static final String ENTITY_ID = "id";
    private static final String ENTITY_PROJECT_ID = "project_id";
    private static final String ENTITY_NAME = "name";
    private static final String ENTITY_DESCRIPTION = "description";

    private static HashMap<String, Object> extractProjectEntity(ResultSet resultSet) throws SQLException {
        return new HashMap<>() {{
            put(ENTITY_ID, resultSet.getObject(ENTITY_ID));
            put(ENTITY_PROJECT_ID, resultSet.getString(ENTITY_PROJECT_ID));
            put(ENTITY_NAME, resultSet.getString(ENTITY_NAME));
            put(ENTITY_DESCRIPTION, resultSet.getString(ENTITY_DESCRIPTION));
        }};
    }

    /**
     *
     * @param resultSet query result with data
     * @return built Event instance
     * @throws SQLException exception which throws when something goes wrong with extracting fields
     */
    public static Event extractEvent(ResultSet resultSet) throws SQLException {
        HashMap<String, Object> entityFields = extractProjectEntity(resultSet);
        UUID authorId = (UUID) resultSet.getObject("author_id");
        String authorFullName = resultSet.getString("author_full_name");
        Timestamp startDate = resultSet.getTimestamp("start_date");
        Timestamp endDate = resultSet.getTimestamp("end_date");
        Timestamp createdAt = resultSet.getTimestamp("created_at");

        LocalDateTime eventStartDate = null;
        LocalDateTime eventEndDate = null;
        LocalDateTime eventCreatedAt = null;

        if (startDate != null) {
            eventStartDate = startDate.toLocalDateTime();
        }
        if (endDate != null) {
            eventEndDate = endDate.toLocalDateTime();
        }
        if (createdAt != null)
            eventCreatedAt = createdAt.toLocalDateTime().withNano(0);

        return new Event(
                (String) entityFields.get(ENTITY_NAME),
                (String) entityFields.get(ENTITY_DESCRIPTION),
                (UUID) entityFields.get(ENTITY_ID),
                (String) entityFields.get(ENTITY_PROJECT_ID),
                authorId,
                authorFullName,
                eventCreatedAt,
                eventStartDate,
                eventEndDate
        );
    }

    public static Documentation extractDocumentation(ResultSet resultSet) throws SQLException {
        HashMap<String, Object> entityFields = extractProjectEntity(resultSet);
        UUID documentationAuthorId = (UUID) resultSet.getObject("author_id");
        String documentationAuthorFullName = resultSet.getString("author_full_name");
        String[] documentationArticleTitles = (String[]) resultSet.getArray("article_titles").getArray();
        String[] documentationArticles = (String[]) resultSet.getArray("articles").getArray();
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        LocalDateTime documentationCreatedAt = null;

        if (createdAt != null) {
            documentationCreatedAt = createdAt.toLocalDateTime().withNano(0);
        }

        HashMap<String, String> body = convertDocumentationBodyToHashMap(documentationArticleTitles, documentationArticles);

        return new Documentation(
                (String) entityFields.get(ENTITY_NAME),
                (String) entityFields.get(ENTITY_DESCRIPTION),
                (UUID) entityFields.get(ENTITY_ID),
                (String) entityFields.get(ENTITY_PROJECT_ID),
                documentationAuthorId,
                documentationAuthorFullName,
                documentationCreatedAt,
                body
        );
    }

    /**
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static Employee extractEmployee(ResultSet resultSet) throws SQLException {
        LocalDate employeeBirthday = null;
        Date birthday = resultSet.getDate("birthday");
        if (birthday != null) {
            employeeBirthday = birthday.toLocalDate();
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
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static BugReport extractBugReport(ResultSet resultSet) throws SQLException {
        HashMap<String, Object> entityFields = extractProjectEntity(resultSet);
        BugStatus bugReportStatus = BugStatus.valueOf(resultSet.getString("status"));
        Priority bugReportPriority = Priority.valueOf(resultSet.getString("priority"));
        UUID bugReportAuthorId = (UUID) resultSet.getObject("author_id");
        String bugReportAuthorFullName = resultSet.getString("author_full_name");
        Timestamp createdAt = resultSet.getTimestamp("created_at");

        LocalDateTime bugReportCreatedAt = null;
        if (createdAt != null) {
            bugReportCreatedAt = createdAt.toLocalDateTime().withNano(0);
        }

        return new BugReport(
                (String) entityFields.get(ENTITY_NAME),
                (String) entityFields.get(ENTITY_DESCRIPTION),
                (UUID) entityFields.get(ENTITY_ID),
                (String) entityFields.get(ENTITY_PROJECT_ID),
                bugReportAuthorId,
                bugReportAuthorFullName,
                bugReportCreatedAt,
                bugReportPriority,
                bugReportStatus
        );
    }

    /**
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static Task extractTask(ResultSet resultSet) throws SQLException {
        HashMap<String, Object> entityFields = extractProjectEntity(resultSet);
        UUID taskExecutorId = (UUID) resultSet.getObject("executor_id");
        String taskExecutorFullName = resultSet.getString("executor_full_name");
        String taskComment = resultSet.getString("comment");
        Priority taskPriority = Priority.valueOf(resultSet.getString("priority"));
        String[] taskTags = (String[]) resultSet.getArray("tag").getArray();
        ArrayList<String> tags = new ArrayList<>(Arrays.asList(taskTags));
        WorkStatus taskStatus = WorkStatus.valueOf(resultSet.getString("status"));

        Timestamp sqlCompletedAt = resultSet.getTimestamp("completed_at");
        LocalDateTime taskCompletedAt = null;
        if (sqlCompletedAt != null)
            taskCompletedAt = sqlCompletedAt.toLocalDateTime().withNano(0);

        LocalDateTime taskDeadline = null;
        Timestamp sqlDeadline = resultSet.getTimestamp("deadline");
        if (sqlDeadline != null) {
            taskDeadline = sqlDeadline.toLocalDateTime();
        }

        Timestamp date = resultSet.getTimestamp("created_at");
        LocalDateTime taskCreatedAt = null;
        if (date != null) {
            taskCreatedAt = date.toLocalDateTime().withNano(0);
        }

        return new Task(
                (String) entityFields.get(ENTITY_NAME),
                (String) entityFields.get(ENTITY_DESCRIPTION),
                (UUID) entityFields.get(ENTITY_ID),
                taskExecutorId,
                taskExecutorFullName,
                (String) entityFields.get(ENTITY_PROJECT_ID),
                taskDeadline,
                taskComment,
                taskPriority,
                tags,
                taskStatus,
                taskCreatedAt,
                taskCompletedAt
        );
    }

    public static Project extractProject(ResultSet resultSet) throws SQLException {
        DataProvider postgresProvider = new PostgresDataProvider();

        String projectId = resultSet.getString("id");
        String projectName = resultSet.getString("name");
        String projectDescription = resultSet.getString("description");
        WorkStatus projectStatus = WorkStatus.valueOf(resultSet.getString("status"));
        UUID managerId = (UUID) resultSet.getObject("manager_id");
        Employee projectManager = postgresProvider.getEmployeeById(managerId).getData();
        ArrayList<Employee> projectTeam = postgresProvider.getProjectTeam(projectId).getData();
        ArrayList<Task> projectTasks = postgresProvider.getTasksByProjectId(projectId).getData();
        ArrayList<ProjectEntity> projectDocumentation = new ArrayList<>(List.copyOf(
                postgresProvider.getDocumentationsByProjectId(projectId).getData()
            )
        );
        ArrayList<Event> projectEvents = postgresProvider.getEventsByProjectId(projectId).getData();
        ArrayList<BugReport> projectBugReports = postgresProvider.getBugReportsByProjectId(projectId).getData();

        LocalDateTime projectDeadline = null;
        Timestamp gotTimestamp = resultSet.getTimestamp("deadline");
        if (gotTimestamp != null) {
            projectDeadline = gotTimestamp.toLocalDateTime();
        }

        return new Project(
                projectName,
                projectDescription,
                projectId,
                projectDeadline,
                projectStatus,
                projectManager,
                projectTeam,
                // преобразование в список ProjectEntity
                new ArrayList<>(){{ addAll(projectTasks); }},
                new ArrayList<>(){{ addAll(projectBugReports); }},
                new ArrayList<>(){{ addAll(projectEvents); }},
                projectDocumentation
        );
    }

    private static HashMap<String, String> convertDocumentationBodyToHashMap(String[] articleTitles, String[] articles) throws IllegalArgumentException {
        HashMap<String, String> body = new HashMap<>();
        if (articleTitles.length != articles.length)
            throw new IllegalArgumentException("The number of headings and articles does not match");

        for (int i = 0; i < articleTitles.length; i++) {
            body.put(articleTitles[i], articles[i]);
        }

        return body;
    }
}
