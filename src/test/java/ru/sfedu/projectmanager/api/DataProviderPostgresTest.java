package ru.sfedu.projectmanager.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.BugStatus;
import ru.sfedu.projectmanager.model.enums.Priority;
import ru.sfedu.projectmanager.model.enums.WorkStatus;
import ru.sfedu.projectmanager.utils.ResultCode;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class DataProviderPostgresTest {
    private final static DataProviderPostgres postgresProvider = new DataProviderPostgres(Environment.TEST);
    private final static Logger logger = LogManager.getLogger(DataProviderPostgresTest.class);
    private static final ArrayList<Employee> team = new ArrayList<>();
    private static final Employee employee = new Employee(
            "Nikolay",
            "Eremeev",
            new GregorianCalendar(1999, Calendar.MAY, 24),
            "Senior mobile dev lead"
    );

    static Project project = new Project(
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

    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final ArrayList<BugReport> bugReports = new ArrayList<>();
    private final Connection connection = postgresProvider.getConnection();


    BugReport bugReport = new BugReport(
            "mobile_bank_report_12-05-2023",
            "this is a bug report description",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            Priority.HIGH
    );

    static Event event = new Event(
            "mobile bank app presentation",
            "show client what we did",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            new GregorianCalendar(),
            new GregorianCalendar(2023, Calendar.NOVEMBER, 30)
    );

    private final static ArrayList<Event> events = new ArrayList<>(){{add(event);}};

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

    @BeforeAll
    public static void createTaskList() {
        Task task1 = new Task(
                "Task 1",
                "Description for Task 1",
                UUID.fromString("f39369ef-e5a9-4de6-b27e-a305f63f71e5"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                Calendar.getInstance(),
                "Comment for Task 1",
                Priority.HIGH,
                "Tag1",
                WorkStatus.IN_PROGRESS,
                new Date()
        );

        Task task2 = new Task(
                "Task 2",
                "Description for Task 2",
                UUID.fromString("f8480200-6e0c-4ef4-815c-225e6bc0aa66"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                Calendar.getInstance(),
                "Comment for Task 2",
                Priority.MEDIUM,
                "Tag2",
                WorkStatus.COMPLETED,
                new Date()
        );

        Task task3 = new Task(
                "Task 3",
                "Description for Task 3",
                UUID.fromString("b0a274fe-9005-4491-9b4b-0f0882e4f879"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                Calendar.getInstance(),
                "Comment for Task 3",
                Priority.LOW,
                "Tag3",
                WorkStatus.IN_PROGRESS,
                new Date()
        );

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

    @BeforeAll
    static void createTeamList() {
        Calendar birthday1 = new GregorianCalendar(1990, Calendar.JANUARY, 15);
        Employee employee1 = new Employee("Иван", "Иванов", "Иванович", birthday1, "Разработчик");
        team.add(employee1);

        Calendar birthday2 = new GregorianCalendar(1985, Calendar.MAY, 20);
        Employee employee2 = new Employee("Елена", "Петрова", "Александровна", birthday2, "Тестировщик");
        team.add(employee2);

        Calendar birthday3 = new GregorianCalendar(1992, Calendar.AUGUST, 8);
        Employee employee3 = new Employee("Андрей", "Смирнов", null, birthday3, "Дизайнер");
        team.add(employee3);
    }

    @BeforeAll
    static void createBugReportList() {
        BugReport bugReport1 = new BugReport("Bug 1", "Description 1", employee.getId(), employee.getFullName(), project.getId());
        BugReport bugReport2 = new BugReport("Bug 2", "Description 2", employee.getId(), employee.getFullName(), project.getId(), Priority.HIGH);
        BugReport bugReport3 = new BugReport("Bug 3", "Description 3", employee.getId(), employee.getFullName(), project.getId(), Priority.MEDIUM);
        bugReport3.setStatus(BugStatus.CLOSED);

        bugReports.add(bugReport1);
        bugReports.add(bugReport2);
        bugReports.add(bugReport3);
    }


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
                Constants.EMPLOYEE_PROJECT_TABLE_NAME
        };

        for (String query : queries)
            truncateTable(statement, query);

        postgresProvider.processNewProject(project);
        postgresProvider.processNewEmployee(employee);
    }



    @Test
    void getConnection() {
        Connection connection = postgresProvider.getConnection();
        assertNotNull(connection);
    }

    static void truncateTable(Statement statement, String dbName) throws SQLException {
        statement.executeUpdate("TRUNCATE TABLE " + dbName + " CASCADE");
    }

    @Test
    void processNewProject() throws SQLException {
        truncateTable(connection.createStatement(), Constants.PROJECT_TABLE_NAME);
        Result<?> result = postgresProvider.processNewProject(project);
        assertEquals(ResultCode.ACCESS, result.getCode());
    }

    @Test
    void processNewTask() {
        for (Task task : tasks) {
            Result<?> createTaskResult = postgresProvider.processNewTask(task);
            assertEquals(ResultCode.ACCESS, createTaskResult.getCode());
        }
    }

    @Test
    void processNewBugReport() {
        Result<?> createBugReport = postgresProvider.processNewBugReport(bugReport);
        assertEquals(ResultCode.ACCESS, createBugReport.getCode());
    }

    @Test
    void processNewEmployee() throws SQLException {
        truncateTable(postgresProvider.getConnection().createStatement(), "employees");
        Result<?> result = postgresProvider.processNewEmployee(employee);
        assertEquals(ResultCode.ACCESS, result.getCode());
    }

    @Test
    void processNewDocumentation() {
        Result<?> createDocumentationResult = postgresProvider.processNewDocumentation(documentation);
        assertEquals(ResultCode.ACCESS, createDocumentationResult.getCode());
    }

    @Test
    void processNewEvent() {
        Result<?> createEventResult = postgresProvider.processNewEvent(event);
        assertEquals(ResultCode.ACCESS, createEventResult.getCode());
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
        Result<?> bindManagerResult = postgresProvider
                .bindProjectManager(employee.getId(), project.getId());
        assertEquals(ResultCode.ACCESS, bindManagerResult.getCode());
    }

    @Test
    void bindTaskExecutor() {
        Employee newExecutor = new Employee(
                "Alexey",
                "Zaycev",
                new GregorianCalendar(1994, Calendar.FEBRUARY, 1),
                "Senior backend dev"
        );
        Result<?> createEmployeeResult = postgresProvider.processNewEmployee(newExecutor);
        assertEquals(ResultCode.ACCESS, createEmployeeResult.getCode());

        Result<?> bindEmployeeResult = postgresProvider.bindEmployeeToProject(newExecutor.getId(), project.getId());
        assertEquals(ResultCode.ACCESS, bindEmployeeResult.getCode());

        Result<?> createTaskResult = postgresProvider.processNewTask(task);
        assertEquals(ResultCode.ACCESS, createTaskResult.getCode());

        Result<?> bindTaskExecutorResult = postgresProvider.bindTaskExecutor(
                newExecutor.getId(), newExecutor.getFullName(), task.getId(), project.getId()
        );

        assertEquals(ResultCode.ACCESS, bindTaskExecutorResult.getCode());
    }


    @Test
    void bindEmployeeToProject() {
        for (Employee employee : team) {
            Result<?> createEmployeeResult = postgresProvider.processNewEmployee(employee);
            assertEquals(ResultCode.ACCESS, createEmployeeResult.getCode());

            Result<?> bindResult = postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
            assertEquals(ResultCode.ACCESS, bindResult.getCode());
        }
    }

    @Test
    void deleteProject() {
        Result<?> deleteProjectResult = postgresProvider.deleteProject(project.getId());
        assertEquals(ResultCode.ACCESS, deleteProjectResult.getCode());
    }

    @Test
    void deleteTask() {
        Result<?> deleteTaskResult = postgresProvider.deleteTask(tasks.get(0).getId());
        assertEquals(ResultCode.ACCESS, deleteTaskResult.getCode());
    }

    @Test
    void deleteBugReport() {
        Result<?> deleteBugReportResult = postgresProvider.deleteBugReport(bugReport.getId());
        assertEquals(ResultCode.ACCESS, deleteBugReportResult.getCode());
    }

    @Test
    void deleteEvent() {
        Result<?> deleteEventResult = postgresProvider.deleteEvent(event.getId());
        assertEquals(ResultCode.ACCESS, deleteEventResult.getCode());
    }

    @Test
    void deleteDocumentation() {
        Result<?> deleteDocumentationResult = postgresProvider.deleteDocumentation(documentation.getId());
        assertEquals(ResultCode.ACCESS, deleteDocumentationResult.getCode());
    }

    @Test
    void deleteEmployee() {
        Result<?> deleteEmployeeResult = postgresProvider.deleteEmployee(employee.getId());
        assertEquals(ResultCode.ACCESS, deleteEmployeeResult.getCode());
    }

    @Test
    void getTasksByProjectId() {
        for (Task task : tasks) {
            Result<?> createTaskResult = postgresProvider.processNewTask(task);
            assertEquals(ResultCode.ACCESS, createTaskResult.getCode());
        }

        Result<ArrayList<ProjectEntity>> result = postgresProvider.getTasksByProjectId(project.getId());
        logger.debug("getTasksByProjectId[1]: actual {}", result.getData());
        logger.debug("getTasksByProjectId[2]: expected {}", tasks);

        logger.debug("{} = {}", result.getData().get(0).getId(), tasks.get(0).getId());
        assertEquals(ResultCode.ACCESS, result.getCode());
    }

    @Test
    void getProjectById() {
        Result<Project> result = postgresProvider.getProjectById(project.getId());
        logger.debug("getProjectById[1]: actual {}", result.getData());
        logger.debug("getProjectById[2]: expected {}", project);
        assertEquals(project, result.getData());
    }

    @Test
    void getProjectEntityById() {
    }

    @Test
    void getTaskById() {
        processNewTask();
        for (ProjectEntity task : tasks) {
            Result<ProjectEntity> taskResult = postgresProvider.getTaskById(task.getId());
            assertEquals(ResultCode.ACCESS, taskResult.getCode());
            assertEquals(task, taskResult.getData());
        }
    }

    @Test
    void getBugReportsByProjectId() {
        for (BugReport bugReport : bugReports) {
            Result<?> createBugReportsResult = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.ACCESS, createBugReportsResult.getCode());
        }

        Result<ArrayList<ProjectEntity>> getBugReportsResult = postgresProvider.getBugReportsByProjectId(project.getId());
        assertEquals(ResultCode.ACCESS, getBugReportsResult.getCode());
        assertEquals(bugReports, getBugReportsResult.getData());
    }

    @Test
    void getBugReportById() {
        processNewBugReport();
        Result<ProjectEntity> getBugReportResult = postgresProvider.getBugReportById(bugReport.getId());

        assertEquals(ResultCode.ACCESS, getBugReportResult.getCode());
        assertEquals(bugReport, getBugReportResult.getData());
    }

    @Test
    void getEventsByProjectId() {
        processNewEvent();
        Result<ArrayList<ProjectEntity>> getEventsResult = postgresProvider.getEventsByProjectId(project.getId());
        assertEquals(ResultCode.ACCESS, getEventsResult.getCode());
        assertEquals(events, getEventsResult.getData());
    }

    @Test
    void getEventById() {
        processNewEvent();
        Result<ProjectEntity> getEventResult = postgresProvider.getEventById(event.getId());

        assertEquals(ResultCode.ACCESS, getEventResult.getCode());
        assertEquals(event, getEventResult.getData());
    }

    @Test
    void getDocumentationByProjectId() {
        processNewDocumentation();
        Result<ProjectEntity> getDocResult = postgresProvider.getDocumentationByProjectId(documentation.getProjectId());

        logger.debug("getDocumentationByProjectId[1]: expected {}", documentation);
        logger.debug("getDocumentationByProjectId[2]: actual {}", getDocResult.getData());

        assertEquals(ResultCode.ACCESS, getDocResult.getCode());
        assertEquals(documentation, getDocResult.getData());
    }

    @Test
    void getProjectTeam() {
        bindEmployeeToProject();
        Result<ArrayList<Employee>> getTeamResult = postgresProvider.getProjectTeam(project.getId());

        logger.debug("getProjectTeam[1]: expected {}", team);
        logger.debug("getProjectTeam[2]: actual {}", getTeamResult.getData());
        assertEquals(ResultCode.ACCESS, getTeamResult.getCode());
        assertEquals(team, getTeamResult.getData());
    }

    @Test
    void getEmployee() throws SQLException {
        processNewEmployee();
        Result<Employee> result = postgresProvider.getEmployeeById(employee.getId());

        logger.debug("getEmployee[1]: expected {}", employee);
        logger.debug("getEmployee[2]: actual {}", result.getData());
        assertEquals(ResultCode.ACCESS, result.getCode());
        assertEquals(employee, result.getData());
    }
}