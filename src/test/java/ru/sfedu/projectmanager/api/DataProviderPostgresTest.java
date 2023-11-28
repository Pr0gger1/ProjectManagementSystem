package ru.sfedu.projectmanager.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.Priority;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataProviderPostgresTest {
    private final static DataProviderPostgres postgresProvider = new DataProviderPostgres(Environment.TEST);
    private final Employee employee = new Employee(
            "Nikolay",
            "Eremeev",
            new GregorianCalendar(1999, Calendar.MAY, 24),
            "Senior mobile dev lead"
    );

    Project project = new Project(
            "mobile bank app",
            "mobile app for bank based on kotlin and swift",
            "mobile_bank"
    );

    Task task = new Task(
            "create main page of application",
            "main page of bank application",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            Priority.MEDIUM
    );

    BugReport bugReport = new BugReport(
            "mobile_bank_report_12-05-2023",
            "this is a bug report description",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            Priority.HIGH
    );

    Event event = new Event(
            "mobile bank app presentation",
            "show client what we did",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            new GregorianCalendar(),
            new GregorianCalendar(2023, Calendar.NOVEMBER, 30)
    );

    Documentation documentation = new Documentation(
            "app documentation",
            "app documentation description",
            new HashMap<>() {{
                put("chapter one", "some loooooong text");
                put("chapter two", "another some loooooong text");
            }},
            employee.getId(),
            employee.getFullName(),
            project.getId()
    );

    @BeforeEach
    void resetDb() throws SQLException {
        Connection connection = postgresProvider.getConnection();
        Statement statement = connection.createStatement();
        String[] queries = {
                Constants.PROJECT_TABLE_NAME,
                Constants.EVENTS_TABLE_NAME,
                Constants.TASKS_TABLE_NAME,
                Constants.EMPLOYEES_TABLE_NAME,
                Constants.BUG_REPORTS_TABLE_NAME,
                Constants.DOCUMENTATIONS_TABLE_NAME,
                Constants.PROJECT_EMPLOYEE_TABLE_NAME
        };

        for (String query : queries)
            truncateTable(statement, query);

        processNewProject();

    }


    @Test
    void getConnection() {
        Connection connection = postgresProvider.getConnection();
        assertNotNull(connection);
    }

    void truncateTable(Statement statement, String dbName) throws SQLException {
        statement.executeUpdate("TRUNCATE TABLE " + dbName + " CASCADE");
    }

    void processNewProject() {
        boolean createEmployeeResult = postgresProvider.processNewEmployee(employee);
        assertTrue(createEmployeeResult);

        project.setManager(employee);
        project.setDeadline(new GregorianCalendar(2024, Calendar.SEPTEMBER, 10));
        boolean result = postgresProvider.processNewProject(project);

        assertTrue(result);
    }

    @Test
    void processNewTask() {
        boolean createTaskResult = postgresProvider.processNewTask(task);
        assertTrue(createTaskResult);
    }

    @Test
    void processNewBugReport() {
        boolean createBugReport = postgresProvider.processNewBugReport(bugReport);
        assertTrue(createBugReport);
    }

    @Test
    void processNewEmployee() {
        boolean result = postgresProvider.processNewEmployee(employee);
        assertTrue(result);
    }

    @Test
    void processNewDocumentation() {
        boolean createDocumentationResult = postgresProvider.processNewDocumentation(documentation);
        assertTrue(createDocumentationResult);
    }

    @Test
    void processNewEvent() {
        boolean createEventResult = postgresProvider.processNewEvent(event);
        assertTrue(createEventResult);
    }

    @Test
    void trackTaskStatus() {
    }

    @Test
    void monitorProjectReadiness() {
    }

    @Test
    void calculateProjectReadiness() {
    }

    @Test
    void calculateLaborEfficiency() {
    }

    @Test
    void trackBugReportStatus() {
    }


    @Test
    void bindProjectManager() {
    }

    @Test
    void bindTaskExecutor() {
    }

    @Test
    void getProjectById() {
    }

    @Test
    void getProjectEntityById() {
    }

    @Test
    void bindEntityToProject() {
    }

    @Test
    void deleteProject() {
        boolean deleteProjectResult = postgresProvider.deleteProject(project.getId());
        assertTrue(deleteProjectResult);
    }

    @Test
    void deleteTask() {
        boolean deleteTaskResult = postgresProvider.deleteTask(task.getId());
        assertTrue(deleteTaskResult);
    }

    @Test
    void deleteBugReport() {
    }

    @Test
    void deleteEvent() {
    }

    @Test
    void deleteDocumentation() {
    }

    @Test
    void deleteEmployee() {
    }

    @Test
    void testGetProjectById() {
    }

    @Test
    void getTaskByProjectId() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void getBugReportsByProjectId() {
    }

    @Test
    void getBugReportById() {
    }

    @Test
    void getEventsByProjectId() {
    }

    @Test
    void getEventById() {
    }

    @Test
    void getDocumentationByProjectId() {
    }

    @Test
    void getProjectTeam() {
    }

    @Test
    void getEmployee() {
    }

    @Test
    void testGetProjectEntityById() {
    }
}