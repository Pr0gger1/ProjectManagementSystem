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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class DataProviderPostgresTest {
    private final static DataProviderPostgres postgresProvider = new DataProviderPostgres(Environment.TEST);
    private final static Logger logger = LogManager.getLogger(DataProviderPostgresTest.class);
    private static final ArrayList<Employee> team = new ArrayList<>();
    private static final Employee employee = new Employee(
            "Nikolay",
            "Eremeev",
            LocalDate.of(1999, Month.MAY, 6),
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
            LocalDateTime.of(2023, Month.DECEMBER, 22, 15, 0),
            LocalDateTime.of(2023, Month.DECEMBER, 22, 10, 0)
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
                LocalDateTime.of(2023, Month.DECEMBER, 24,0, 0),
                "Comment for Task 1",
                Priority.HIGH,
                "Tag1",
                WorkStatus.COMPLETED,
                LocalDateTime.now().withNano(0),
                LocalDateTime.of(2023, Month.DECEMBER, 20, 15, 12)
        );

        Task task2 = new Task(
                "Task 2",
                "Description for Task 2",
                UUID.fromString("f8480200-6e0c-4ef4-815c-225e6bc0aa66"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                LocalDateTime.of(2023, Month.NOVEMBER, 15,0, 0),
                "Comment for Task 2",
                Priority.MEDIUM,
                "Tag2",
                WorkStatus.COMPLETED,
                LocalDateTime.now().withNano(0),
                LocalDateTime.of(2023, Month.NOVEMBER, 18, 10, 54)
        );

        Task task3 = new Task(
                "Task 3",
                "Description for Task 3",
                UUID.fromString("b0a274fe-9005-4491-9b4b-0f0882e4f879"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                LocalDateTime.of(2023, Month.DECEMBER, 15, 0, 0),
                "Comment for Task 3",
                Priority.LOW,
                "Tag3",
                WorkStatus.IN_PROGRESS,
                LocalDateTime.now().withNano(0),
                null
        );

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

    @BeforeAll
    static void createTeamList() {
        LocalDate birthday1 = LocalDate.of(1990, Month.MAY, 15);
        Employee employee1 = new Employee("Иван", "Иванов", "Иванович", birthday1, "Разработчик");
        team.add(employee1);

        LocalDate birthday2 = LocalDate.of(1985, Month.MARCH, 14);
        Employee employee2 = new Employee("Елена", "Петрова", "Александровна", birthday2, "Тестировщик");
        team.add(employee2);

        LocalDate birthday3 = LocalDate.of(1992, Month.AUGUST, 7);
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
        logger.debug("getConnection[1]: expected not nullable value");
        logger.debug("getConnection[2]: actual {}", connection);
        assertNotNull(connection);
    }

    static void truncateTable(Statement statement, String dbName) throws SQLException {
        statement.executeUpdate("TRUNCATE TABLE " + dbName + " CASCADE");
    }

    @Test
    void processNewProject() throws SQLException {
        truncateTable(connection.createStatement(), Constants.PROJECT_TABLE_NAME);
        Result<?> createProjectResult = postgresProvider.processNewProject(project);

        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", createProjectResult.getCode());
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());
    }

    @Test
    void processNewTask() {
        for (Task task : tasks) {
            Result<?> createTaskResult = postgresProvider.processNewTask(task);
            logger.debug("processNewTask[1]: expected {}", ResultCode.SUCCESS);
            logger.debug("processNewTask[2]: actual {}", createTaskResult.getCode());
            assertEquals(ResultCode.SUCCESS, createTaskResult.getCode());
        }
    }

    @Test
    void processNewBugReport() {
        for (BugReport bugReport : bugReports) {
            Result<?> createBugReportResult = postgresProvider.processNewBugReport(bugReport);
            logger.debug("processNewBugReport[1]: expected {}", ResultCode.SUCCESS);
            logger.debug("processNewBugReport[2]: actual {}", createBugReportResult.getCode());
            assertEquals(ResultCode.SUCCESS, createBugReportResult.getCode());
        }
    }

    @Test
    void processNewEmployee() throws SQLException {
        truncateTable(postgresProvider.getConnection().createStatement(), "employees");
        Result<?> createEmployeeResult = postgresProvider.processNewEmployee(employee);

        logger.debug("processNewEmployee[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewEmployee[2]: actual {}", createEmployeeResult.getCode());
        assertEquals(ResultCode.SUCCESS, createEmployeeResult.getCode());
    }

    @Test
    void processNewDocumentation() {
        Result<?> createDocumentationResult = postgresProvider.processNewDocumentation(documentation);

        logger.debug("processNewDocumentation[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[2]: actual {}", createDocumentationResult.getCode());
        assertEquals(ResultCode.SUCCESS, createDocumentationResult.getCode());
    }

    @Test
    void processNewEvent() {
        Result<?> createEventResult = postgresProvider.processNewEvent(event);
        logger.debug("processNewEvent[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewEvent[2]: actual {}", createEventResult.getCode());
        assertEquals(ResultCode.SUCCESS, createEventResult.getCode());
    }

    @Test
    void trackTaskStatus() {
        processNewTask();
        processNewBugReport();
        TrackInfo<Task, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            for (Task task : tasks) put(task, task.getStatus().name());
        }});

        TrackInfo<Task, String> trackInfoActual = postgresProvider.trackTaskStatus(project.getId());
        logger.debug("processNewProject[1]: expected {}", trackInfoExpected);
        logger.debug("processNewProject[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    void initDataForMonitorProjectReadiness() {
        processNewTask();
        postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
        for (Task task : tasks) {
            Result<?> bindEmployeeToTaskResult = postgresProvider.bindTaskExecutor(
                    employee.getId(), employee.getFullName(),
                    task.getId(), project.getId()
            );
            assertEquals(ResultCode.SUCCESS, bindEmployeeToTaskResult.getCode());
        }
    }

    @Test
    void monitorProjectReadiness() {
        initDataForMonitorProjectReadiness();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project.getId());
        TrackInfo<String, ?> trackInfo = new TrackInfo<>(new HashMap<>() {{
            put(Constants.TRACK_INFO_KEY_PROJECT_READINESS, projectReadiness);
            put(Constants.TRACK_INFO_KEY_TASK_STATUS, trackTasks);
        }});

        TrackInfo<String, ?> result = postgresProvider.monitorProjectReadiness(project.getId(), false, false);
        assertEquals(trackInfo, result);
    }

    @Test
    void monitorProjectReadinessWithLaborEfficiency() {
        initDataForMonitorProjectReadiness();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project.getId());
        TrackInfo<String, ?> trackInfo = new TrackInfo<>(new HashMap<>() {{
            put(Constants.TRACK_INFO_KEY_PROJECT_READINESS, projectReadiness);
            put(Constants.TRACK_INFO_KEY_TASK_STATUS, trackTasks);
        }});

        TrackInfo<Employee, Float> laborEfficiency = postgresProvider.calculateLaborEfficiency(project.getId());
        trackInfo.addData(Constants.TRACK_INFO_KEY_LABOR_EFFICIENCY, laborEfficiency);

        TrackInfo<String, ?> result = postgresProvider.monitorProjectReadiness(project.getId(), true, false);
        assertEquals(trackInfo, result);
    }

    @Test
    void monitorProjectReadinessWithBugStatusAndLaborEfficiency() {
        initDataForMonitorProjectReadiness();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project.getId());
        TrackInfo<String, ?> trackInfo = new TrackInfo<>(new HashMap<>() {{
            put(Constants.TRACK_INFO_KEY_PROJECT_READINESS, projectReadiness);
            put(Constants.TRACK_INFO_KEY_TASK_STATUS, trackTasks);
        }});

        TrackInfo<Employee, Float> laborEfficiency = postgresProvider.calculateLaborEfficiency(project.getId());
        trackInfo.addData(Constants.TRACK_INFO_KEY_LABOR_EFFICIENCY, laborEfficiency);

        TrackInfo<BugReport, String> bugStatuses = postgresProvider.trackBugReportStatus(project.getId());
        trackInfo.addData(Constants.TRACK_INFO_KEY_BUG_STATUS, bugStatuses);

        TrackInfo<String, ?> result = postgresProvider.monitorProjectReadiness(project.getId(), true, true);
        assertEquals(trackInfo, result);
    }

    @Test
    void calculateProjectReadiness() {
        int completedTasksCount = 2;
        float expectedReadiness = ((float) completedTasksCount / tasks.size()) * 100.0f;
        ArrayList<Task> tasks1 = new ArrayList<>() {{
            add(
                    new Task(
                    "Task 1",
                    "Description for Task 1",
                    UUID.fromString("f39369ef-e5a9-4de6-b27e-a305f63f71e5"),
                    employee.getId(),
                    employee.getFullName(),
                    "mobile_bank",
                    LocalDateTime.of(2023, Month.DECEMBER, 24,0, 0),
                    "Comment for Task 1",
                    Priority.HIGH,
                    "Tag1",
                    WorkStatus.COMPLETED,
                    LocalDateTime.now().withNano(0),
                    null)
            );

            add(
                new Task(
                    "Task 2",
                    "Description for Task 2",
                    UUID.fromString("f8480200-6e0c-4ef4-815c-225e6bc0aa66"),
                    employee.getId(),
                    employee.getFullName(),
                    "mobile_bank",
                    LocalDateTime.of(2023, Month.NOVEMBER, 15,0, 0),
                    "Comment for Task 2",
                    Priority.MEDIUM,
                    "Tag2",
                    WorkStatus.COMPLETED,
                    LocalDateTime.now().withNano(0),
                    null
                )
            );
            add(
                new Task(
                    "Task 3",
                    "Description for Task 3",
                    UUID.fromString("f8480200-6e0c-4ef4-815c-225e6bc0aa48"),
                    employee.getId(),
                    employee.getFullName(),
                    "mobile_bank",
                    LocalDateTime.of(2023, Month.NOVEMBER, 15,0, 0),
                    "Comment for Task 3",
                    Priority.MEDIUM,
                    "Tag2",
                    WorkStatus.IN_PROGRESS,
                    LocalDateTime.now().withNano(0),
                    null
                )
            );
        }};


        for (Task task : tasks1) {
            Result<?> createTaskResult = postgresProvider.processNewTask(task);
            Result<?> bindTaskToEmployee = postgresProvider.bindTaskExecutor(employee.getId(), employee.getFullName(), task.getId(), project.getId());
        }

        float actualReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        logger.debug("processNewProject[1]: expected {}", expectedReadiness);
        logger.debug("processNewProject[2]: actual {}", actualReadiness);
        assertEquals(((float) completedTasksCount / tasks.size()) * 100.0f, actualReadiness);
    }

    @Test
    void calculateLaborEfficiency() {
        ArrayList<Task> tasks1 = new ArrayList<>(tasks);
        Task task1 = tasks1.get(0);
        Task task2 = tasks1.get(1);
        Task task3 = tasks1.get(2);

        task1.setStatus(WorkStatus.COMPLETED);
        task1.setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 15, 15, 30));

        task2.setStatus(WorkStatus.COMPLETED);
        task2.setCompletedAt(LocalDateTime.of(2023, Month.NOVEMBER, 17, 12,42));

        task3.setStatus(WorkStatus.COMPLETED);
        task3.setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 14, 17,24));
        processNewTask();

        Result<?> bindToProjectResult = postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
        assertEquals(ResultCode.SUCCESS, bindToProjectResult.getCode());

        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee, 102.0f);}}
        );

        for (Task task : tasks) {
            Result<?> bindTaskExecutorResult = postgresProvider.bindTaskExecutor(
                    employee.getId(), employee.getFullName(), task.getId(), task.getProjectId()
            );
            assertEquals(ResultCode.SUCCESS, bindTaskExecutorResult.getCode());
        }

        TrackInfo<Employee, Float> result = postgresProvider.calculateLaborEfficiency(project.getId());
        assertEquals(expectedData, result);
    }

    @Test
    void trackBugReportStatus() {
        TrackInfo<BugReport, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            for (BugReport bugReport : bugReports)
                put(bugReport, bugReport.getStatus().name());
        }});
        processNewBugReport();

        TrackInfo<BugReport, String> trackInfoActual = postgresProvider.trackBugReportStatus(project.getId());
        logger.debug("processNewProject[1]: expected {}", trackInfoExpected);
        logger.debug("processNewProject[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }


    @Test
    void bindProjectManager() {
        Result<?> bindManagerResult = postgresProvider
                .bindProjectManager(employee.getId(), project.getId());

        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", bindManagerResult.getCode());
        assertEquals(ResultCode.SUCCESS, bindManagerResult.getCode());
    }

    @Test
    void bindTaskExecutor() {
        Employee newExecutor = new Employee(
                "Alexey",
                "Zaycev",
                LocalDate.of(1994, Month.FEBRUARY, 1),
                "Senior backend dev"
        );
        Result<?> createEmployeeResult = postgresProvider.processNewEmployee(newExecutor);
        assertEquals(ResultCode.SUCCESS, createEmployeeResult.getCode());
        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", createEmployeeResult.getCode());

        Result<?> bindEmployeeResult = postgresProvider.bindEmployeeToProject(newExecutor.getId(), project.getId());
        assertEquals(ResultCode.SUCCESS, bindEmployeeResult.getCode());
        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", bindEmployeeResult.getCode());

        Result<?> createTaskResult = postgresProvider.processNewTask(task);
        assertEquals(ResultCode.SUCCESS, createTaskResult.getCode());
        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", createTaskResult.getCode());

        Result<?> bindTaskExecutorResult = postgresProvider.bindTaskExecutor(
                newExecutor.getId(), newExecutor.getFullName(), task.getId(), project.getId()
        );
        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", bindTaskExecutorResult.getCode());

        assertEquals(ResultCode.SUCCESS, bindTaskExecutorResult.getCode());
    }


    @Test
    void bindEmployeeToProject() {
        for (Employee employee : team) {
            Result<?> createEmployeeResult = postgresProvider.processNewEmployee(employee);
            assertEquals(ResultCode.SUCCESS, createEmployeeResult.getCode());

            Result<?> bindResult = postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
            assertEquals(ResultCode.SUCCESS, bindResult.getCode());
        }
    }

    @Test
    void deleteProject() {
        Result<?> deleteProjectResult = postgresProvider.deleteProject(project.getId());
        assertEquals(ResultCode.SUCCESS, deleteProjectResult.getCode());
    }

    @Test
    void deleteTask() {
        Result<?> deleteTaskResult = postgresProvider.deleteTask(tasks.get(0).getId());
        assertEquals(ResultCode.SUCCESS, deleteTaskResult.getCode());
    }

    @Test
    void deleteBugReport() {
        Result<?> deleteBugReportResult = postgresProvider.deleteBugReport(bugReport.getId());
        assertEquals(ResultCode.SUCCESS, deleteBugReportResult.getCode());
    }

    @Test
    void deleteEvent() {
        Result<?> deleteEventResult = postgresProvider.deleteEvent(event.getId());
        assertEquals(ResultCode.SUCCESS, deleteEventResult.getCode());
    }

    @Test
    void deleteDocumentation() {
        Result<?> deleteDocumentationResult = postgresProvider.deleteDocumentation(documentation.getId());
        assertEquals(ResultCode.SUCCESS, deleteDocumentationResult.getCode());
    }

    @Test
    void deleteEmployee() {
        Result<?> deleteEmployeeResult = postgresProvider.deleteEmployee(employee.getId());
        assertEquals(ResultCode.SUCCESS, deleteEmployeeResult.getCode());
    }

    @Test
    void getTasksByProjectId() {
        for (Task task : tasks) {
            Result<?> createTaskResult = postgresProvider.processNewTask(task);
            assertEquals(ResultCode.SUCCESS, createTaskResult.getCode());
        }

        Result<ArrayList<Task>> result = postgresProvider.getTasksByProjectId(project.getId());
        logger.debug("getTasksByProjectId[1]: actual {}", result.getData());
        logger.debug("getTasksByProjectId[2]: expected {}", tasks);

        logger.debug("{} = {}", result.getData().get(0).getId(), tasks.get(0).getId());
        assertEquals(ResultCode.SUCCESS, result.getCode());
    }

    @Test
    void getTasksByEmployeeId() {
        processNewTask();
        Result<ArrayList<Task>> getTaskByEmployeeResult = postgresProvider.getTasksByEmployeeId(employee.getId());

        logger.debug("getTasksByEmployeeId[1]: expected tasks {}", tasks);
        logger.debug("getTasksByEmployeeId[2]: actual tasks {}", getTaskByEmployeeResult.getData());
        assertEquals(ResultCode.SUCCESS, getTaskByEmployeeResult.getCode());
        assertEquals(tasks, getTaskByEmployeeResult.getData());
    }

    @Test
    void getProjectById() {
        Result<Project> result = postgresProvider.getProjectById(project.getId());
        logger.debug("getProjectById[1]: actual {}", result.getData());
        logger.debug("getProjectById[2]: expected {}", project);
        assertEquals(project, result.getData());
    }

    @Test
    void getTaskById() {
        processNewTask();
        for (Task task : tasks) {
            Result<Task> taskResult = postgresProvider.getTaskById(task.getId());
            assertEquals(ResultCode.SUCCESS, taskResult.getCode());
            assertEquals(task, taskResult.getData());
        }
    }

    @Test
    void getBugReportsByProjectId() {
        for (BugReport bugReport : bugReports) {
            Result<?> createBugReportsResult = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.SUCCESS, createBugReportsResult.getCode());
        }

        Result<ArrayList<BugReport>> getBugReportsResult = postgresProvider.getBugReportsByProjectId(project.getId());
        assertEquals(ResultCode.SUCCESS, getBugReportsResult.getCode());
        assertEquals(bugReports, getBugReportsResult.getData());
    }

    @Test
    void getBugReportById() {
        processNewBugReport();
        Result<BugReport> getBugReportResult = postgresProvider.getBugReportById(bugReports.get(0).getId());

        assertEquals(ResultCode.SUCCESS, getBugReportResult.getCode());
        assertEquals(bugReports.get(0), getBugReportResult.getData());
    }

    @Test
    void getEventsByProjectId() {
        processNewEvent();
        Result<ArrayList<Event>> getEventsResult = postgresProvider.getEventsByProjectId(project.getId());
        assertEquals(ResultCode.SUCCESS, getEventsResult.getCode());
        assertEquals(events, getEventsResult.getData());
    }

    @Test
    void getEventById() {
        processNewEvent();
        Result<Event> getEventResult = postgresProvider.getEventById(event.getId());

        assertEquals(ResultCode.SUCCESS, getEventResult.getCode());
        assertEquals(event, getEventResult.getData());
    }

    @Test
    void getDocumentationByProjectId() {
        processNewDocumentation();
        Result<Documentation> getDocResult = postgresProvider.getDocumentationByProjectId(documentation.getProjectId());

        logger.debug("getDocumentationByProjectId[1]: expected {}", documentation);
        logger.debug("getDocumentationByProjectId[2]: actual {}", getDocResult.getData());

        assertEquals(ResultCode.SUCCESS, getDocResult.getCode());
        assertEquals(documentation, getDocResult.getData());
    }

    @Test
    void getProjectTeam() {
        bindEmployeeToProject();
        Result<ArrayList<Employee>> getTeamResult = postgresProvider.getProjectTeam(project.getId());

        logger.debug("getProjectTeam[1]: expected {}", team);
        logger.debug("getProjectTeam[2]: actual {}", getTeamResult.getData());
        assertEquals(ResultCode.SUCCESS, getTeamResult.getCode());
        assertEquals(team, getTeamResult.getData());
    }

    @Test
    void getEmployee() throws SQLException {
        processNewEmployee();
        Result<Employee> result = postgresProvider.getEmployeeById(employee.getId());

        logger.debug("getEmployee[1]: expected {}", employee);
        logger.debug("getEmployee[2]: actual {}", result.getData());
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals(employee, result.getData());
    }
}