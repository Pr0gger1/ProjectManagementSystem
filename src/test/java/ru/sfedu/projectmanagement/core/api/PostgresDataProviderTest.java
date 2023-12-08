package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostgresDataProviderTest extends BaseTest {
    private static final PostgresDataProvider postgresProvider = new PostgresDataProvider(Environment.TEST);
    private static final Logger logger = LogManager.getLogger(PostgresDataProviderTest.class);
    private static final ArrayList<Employee> team = new ArrayList<>();
    private static final Employee employee = createEmployee(
            "Nikolay",
            "Eremeev",
            "Ivanovich",
            LocalDate.of(1999, Month.MAY, 6),
            "+79882458565",
            "Senior mobile dev lead",
            "mail@mail.ru"
    );

    private static final Project project = createProject(
            "mobile_bank",
            "mobile bank app",
            "mobile app for bank based on kotlin and swift",
            WorkStatus.IN_PROGRESS,
            LocalDateTime.of(2025, Month.MARCH, 1, 0,0),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()

    );

    private static final Task task = createTask(
            project.getId(),
            employee.getId(),
            employee.getFullName(),
            "task",
            "task description",
            "create main page of application",
            WorkStatus.IN_PROGRESS,
            new ArrayList<>(),
            null,
            LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0),
            Priority.MEDIUM
    );

    private static final BugReport bugReport = new BugReport(
            "mobile_bank_report_12-05-2023",
            "this is a bug report description",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            Priority.HIGH
    );

    private static final Event event = new Event(
            "mobile bank app presentation",
            "show client what we did",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            LocalDateTime.of(2023, Month.DECEMBER, 22, 15, 0),
            LocalDateTime.of(2023, Month.DECEMBER, 22, 10, 0)
    );

    private static final Documentation documentation = new Documentation(
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

    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final ArrayList<BugReport> bugReports = new ArrayList<>();
    private final Connection connection = postgresProvider.getConnection();
    private final static ArrayList<Event> events = new ArrayList<>(){{add(event);}};


    @BeforeAll
    public static void createTaskList() {
        Task task1 = createTask(
                project.getId(),
                employee.getId(),
                employee.getFullName(),
                "Task 1",
                "Description for Task 1",
                "Comment for Task 1",
                WorkStatus.COMPLETED,
                new ArrayList<>(Arrays.asList("Tag1", "tag2")),
                LocalDateTime.of(2023, Month.DECEMBER, 20, 15, 12),
                LocalDateTime.of(2023, Month.DECEMBER, 24,0, 0),
                Priority.HIGH
        );

        Task task2 = createTask(
                project.getId(),
                employee.getId(),
                employee.getFullName(),
                "Task 2",
                "Description for Task 2",
                "Comment for Task 2",
                WorkStatus.COMPLETED,
                new ArrayList<>(Arrays.asList("Tag1", "tag2")),
                LocalDateTime.of(2023, Month.NOVEMBER, 18, 10, 54),
                LocalDateTime.of(2023, Month.NOVEMBER, 15,0, 0),
                Priority.MEDIUM
        );

        Task task3 = createTask(
                project.getId(),
                employee.getId(),
                employee.getFullName(),
                "Task 3",
                "Description for Task 3",
                "Comment for Task 3",
                WorkStatus.IN_PROGRESS,
                new ArrayList<>(Arrays.asList("Tag1", "tag2")),
                null,
                LocalDateTime.of(2023, Month.DECEMBER, 15, 0, 0),
                Priority.LOW
        );

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

    @BeforeAll
    static void createTeamList() {
        LocalDate birthday1 = LocalDate.of(1990, Month.MAY, 15);
        Employee employee1 = createEmployee(
                "Иван",
                "Иванов",
                "Иванович",
                birthday1,
                "+79004526387",
                "mail@example.com",
                "Разработчик"
        );
        team.add(employee1);

        LocalDate birthday2 = LocalDate.of(1985, Month.MARCH, 14);
        Employee employee2 = createEmployee(
                "Елена",
                "Петрова",
                "Александровна",
                birthday2,
                "+79004552387",
                "mail@example.com",
                "Тестировщик"
        );
        team.add(employee2);

        LocalDate birthday3 = LocalDate.of(1992, Month.AUGUST, 7);
        Employee employee3 = createEmployee(
                "Андрей",
                "Смирнов",
                null,
                birthday3,
                "+799804526367",
                "mail@example.com",
                "Дизайнер"
        );
        team.add(employee3);
    }

    @BeforeAll
    static void createBugReportList() {
        BugReport bugReport1 = createBugReport(
                "Bug 1",
                "Description 1",
                project.getId(),
                employee.getId(),
                employee.getFullName(),
                BugStatus.IN_PROGRESS,
                Priority.LOW
        );

        BugReport bugReport2 = createBugReport(
                "Bug 2",
                "Description 2",
                project.getId(),
                employee.getId(),
                employee.getFullName(),
                BugStatus.OPENED,
                Priority.HIGH
        );

        BugReport bugReport3 = createBugReport(
                "Bug 3",
                "Description 3",
                project.getId(),
                employee.getId(),
                employee.getFullName(),
                BugStatus.CLOSED,
                Priority.MEDIUM
        );
        bugReport3.setStatus(BugStatus.CLOSED);

        bugReports.add(bugReport1);
        bugReports.add(bugReport2);
        bugReports.add(bugReport3);
    }

    private void initProjectAndEmployee() {
        postgresProvider.processNewProject(project);
        postgresProvider.processNewEmployee(employee);
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

        initProjectAndEmployee();
    }

    @Test
    void getConnection() {
        Connection connection = postgresProvider.getConnection();
        assertNotNull(connection);

        logger.debug("getConnection[2]: actual {}", connection);
        logger.debug("getConnection[1]: expected not null value");
    }

    static void truncateTable(Statement statement, String dbName) throws SQLException {
        statement.executeUpdate("TRUNCATE TABLE " + dbName + " CASCADE");
    }


    @Order(1)
    @Test
    void processNewProject() throws SQLException {
        truncateTable(connection.createStatement(), Constants.PROJECT_TABLE_NAME);
        Result<?> createProjectResult = postgresProvider.processNewProject(project);

        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        logger.debug("processNewProject[2]: actual result code {}", createProjectResult.getCode());
        logger.debug("processNewProject[1]: expected result code {}", ResultCode.SUCCESS);
    }

    @Order(2)
    @Test
    void processNewEmployee() throws SQLException {
        truncateTable(postgresProvider.getConnection().createStatement(), "employees");
        Result<?> createEmployeeResult = postgresProvider.processNewEmployee(employee);

        assertEquals(ResultCode.SUCCESS, createEmployeeResult.getCode());

        logger.debug("processNewEmployee[2]: actual result code {}", createEmployeeResult.getCode());
        logger.debug("processNewEmployee[1]: expected result code {}", ResultCode.SUCCESS);
    }


    @Order(3)
    @Test
    void processNewTask() {
        for (Task task : tasks) {
            Result<?> createTaskResult = postgresProvider.processNewTask(task);
            assertEquals(ResultCode.SUCCESS, createTaskResult.getCode());

            logger.debug("processNewTask[2]: actual result code {}", createTaskResult.getCode());
            logger.debug("processNewTask[1]: expected result code {}", ResultCode.SUCCESS);
        }
    }

    @Order(4)
    @Test
    void processNewBugReport() {
        for (BugReport bugReport : bugReports) {
            Result<?> createBugReportResult = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.SUCCESS, createBugReportResult.getCode());

            logger.debug("processNewBugReport[2]: actual result code {}", createBugReportResult.getCode());
            logger.debug("processNewBugReport[1]: expected result code {}", ResultCode.SUCCESS);
        }
    }

    @Order(5)
    @Test
    void processNewDocumentation() {
        Result<?> createDocumentationResult = postgresProvider.processNewDocumentation(documentation);

        logger.debug("processNewDocumentation[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[2]: actual {}", createDocumentationResult.getCode());
        assertEquals(ResultCode.SUCCESS, createDocumentationResult.getCode());
    }

    @Order(6)
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
        logger.debug("trackTaskStatus[1]: expected {}", trackInfoExpected);
        logger.debug("trackTaskStatus[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    void initDataForMonitorProjectCharacteristics() {
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
        initDataForMonitorProjectCharacteristics();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project.getId());
        TrackInfo<String, ?> trackInfo = new TrackInfo<>(new HashMap<>() {{
            put(Constants.TRACK_INFO_KEY_PROJECT_READINESS, projectReadiness);
            put(Constants.TRACK_INFO_KEY_TASK_STATUS, trackTasks);
        }});

        TrackInfo<String, ?> result = postgresProvider.monitorProjectCharacteristics(project.getId(), false, false);
        assertEquals(trackInfo, result);
    }

    @Test
    void monitorProjectReadinessWithLaborEfficiency() {
        initDataForMonitorProjectCharacteristics();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project.getId());
        TrackInfo<String, ?> trackInfo = new TrackInfo<>(new HashMap<>() {{
            put(Constants.TRACK_INFO_KEY_PROJECT_READINESS, projectReadiness);
            put(Constants.TRACK_INFO_KEY_TASK_STATUS, trackTasks);
        }});

        TrackInfo<Employee, Float> laborEfficiency = postgresProvider.calculateLaborEfficiency(project.getId());
        trackInfo.addData(Constants.TRACK_INFO_KEY_LABOR_EFFICIENCY, laborEfficiency);

        TrackInfo<String, ?> result = postgresProvider.monitorProjectCharacteristics(project.getId(), true, false);
        assertEquals(trackInfo, result);
    }

    @Test
    void monitorProjectReadinessWithBugStatusAndLaborEfficiency() {
        initDataForMonitorProjectCharacteristics();
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

        TrackInfo<String, ?> result = postgresProvider.monitorProjectCharacteristics(project.getId(), true, true);
        assertEquals(trackInfo, result);
    }

    @Test
    void calculateProjectReadiness() {
        ArrayList<Task> tasks1 = new ArrayList<>() {{ addAll(tasks); }};
        int completedTasksCount = (int) tasks1.stream()
                .filter(task -> task.getStatus() == WorkStatus.COMPLETED).count();
        float expectedReadiness = ((float) completedTasksCount / tasks.size()) * 100.0f;
        for (Task task : tasks1) {
            postgresProvider.processNewTask(task);
            postgresProvider.bindTaskExecutor(employee.getId(), employee.getFullName(), task.getId(), project.getId());
        }

        float actualReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        logger.debug("processNewProject[1]: expected {}", expectedReadiness);
        logger.debug("processNewProject[2]: actual {}", actualReadiness);
        assertEquals(expectedReadiness, actualReadiness);
    }

    @Test
    void calculateProjectReadinessIfHasNoTasks() {
        float actualReadiness = postgresProvider.calculateProjectReadiness(project.getId());
        float expectedReadiness = 0f;
        logger.debug("calculateProjectReadinessIfHasNoTasks[1]: actual project readiness: {}", actualReadiness);
        logger.debug("calculateProjectReadinessIfHasNoTasks[2]: expected project readiness {}", expectedReadiness);
        assertEquals(expectedReadiness, actualReadiness);
    }

    @Test
    void calculateLaborEfficiency() {
        ArrayList<Task> tasks1 = new ArrayList<>() {{addAll(tasks);}};
        tasks1.forEach(task -> {
            task.setStatus(WorkStatus.COMPLETED);
        });

        tasks1.get(0).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 15, 15, 30));
        tasks1.get(1).setCompletedAt(LocalDateTime.of(2023, Month.NOVEMBER, 17, 12,42));
        tasks1.get(2).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 14, 17,24));
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

        TrackInfo<Employee, Float> actual = postgresProvider.calculateLaborEfficiency(project.getId());
        assertEquals(expectedData, actual);
    }

    @Test
    void calculateLaborEfficiencyIfEmployeeHasNoTasks() {
        postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee, 0f);}}
        );

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
        postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
        Result<?> actual = postgresProvider
                .bindProjectManager(employee.getId(), project.getId());

        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", actual.getCode());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Test
    void bindTaskExecutor() {
        Employee newExecutor = new Employee(
                "Alexey",
                "Zaycev",
                LocalDate.of(1994, Month.FEBRUARY, 1),
                "Senior backend dev"
        );
        postgresProvider.processNewEmployee(newExecutor);
        postgresProvider.bindEmployeeToProject(newExecutor.getId(), project.getId());
        postgresProvider.processNewTask(task);

        Result<?> actual = postgresProvider.bindTaskExecutor(
                newExecutor.getId(), newExecutor.getFullName(), task.getId(), project.getId()
        );

        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", actual.getCode());
    }


    @Test
    void bindEmployeeToProject() {
        for (Employee employee : team) {
            postgresProvider.processNewEmployee(employee);

            Result<?> bindResult = postgresProvider.bindEmployeeToProject(employee.getId(), project.getId());
            assertEquals(ResultCode.SUCCESS, bindResult.getCode());
        }
    }

    @Test
    void deleteProject() {
        Result<?> actual = postgresProvider.deleteProject(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteProject[1]: delete project actual: {}", actual.getCode());
        logger.debug("deleteProject[2]: delete project expected: {}", ResultCode.SUCCESS);
    }

    @Test
    void deleteNonExistentProject() {
        Result<?> actual = postgresProvider.deleteProject("some nonexistent project");
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentProject[1]: delete nonexistent project actual: {}", actual.getCode());
        logger.debug("deleteNonExistentProject[2]: delete nonexistent project expected: {}", ResultCode.NOT_FOUND);
    }

    @Test
    void deleteTask() {
        postgresProvider.processNewTask(task);

        Result<?> actual = postgresProvider.deleteTask(task.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteTask[1]: delete task actual: {}", actual.getCode());
        logger.debug("deleteTask[2]: delete task expected: {}", ResultCode.SUCCESS);
    }

    @Test
    void deleteNonExistentTask() {
        Result<?> actual = postgresProvider.deleteTask(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentTask[1]: delete nonexistent task actual: {}", actual.getCode());
        logger.debug("deleteNonExistentTask[2]: delete nonexistent task expected: {}", ResultCode.NOT_FOUND);
    }

    @Test
    void deleteBugReport() {
        postgresProvider.processNewBugReport(bugReport);
        Result<?> actual = postgresProvider.deleteBugReport(bugReport.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteBugReport[1]: delete bug report actual {}", actual.getCode());
        logger.debug("deleteBugReport[2]: delete bug report expected {}", ResultCode.SUCCESS);
    }

    @Test
    void deleteNonExistentBugReport() {
        Result<?> actual = postgresProvider.deleteBugReport(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentBugReport[1]: delete nonexistent bug report actual {}", actual.getCode());
        logger.debug("deleteNonExistentBugReport[2]: delete nonexistent bug report expected {}", ResultCode.NOT_FOUND);
    }

    @Test
    void deleteEvent() {
        postgresProvider.processNewEvent(event);

        Result<?> actual = postgresProvider.deleteEvent(event.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteBugReport[1]: delete existent event actual {}", actual.getCode());
        logger.debug("deleteBugReport[2]: delete existent event expected {}", ResultCode.SUCCESS);
    }

    @Test
    void deleteNonExistentEvent() {
        Result<?> actual = postgresProvider.deleteEvent(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentEvent[1]: delete nonexistent event actual {}", actual.getCode());
        logger.debug("deleteNonExistentEvent[2]: delete nonexistent event expected {}", ResultCode.NOT_FOUND);
    }

    @Test
    void deleteDocumentation() {
        postgresProvider.processNewDocumentation(documentation);

        Result<?> actual = postgresProvider.deleteDocumentation(documentation.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteDocumentation[1]: delete documentation actual {}", actual.getCode());
        logger.debug("deleteDocumentation[2]: delete documentation expected {}", ResultCode.SUCCESS);
    }

    @Test
    void deleteNonExistentDocumentation() {
        Result<?> actual = postgresProvider.deleteDocumentation(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentDocumentation[1]: delete nonexistent documentation actual {}", actual.getCode());
        logger.debug("deleteNonExistentBugReport[2]: delete nonexistent documentation expected {}", ResultCode.NOT_FOUND);
    }

    @Test
    void deleteEmployee() {
        Result<?> actual = postgresProvider.deleteEmployee(employee.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteEmployee[1]: delete employee actual {}", actual.getCode());
        logger.debug("deleteEmployee[2]: delete employee expected {}", ResultCode.SUCCESS);
    }

    @Test
    void deleteNonExistentEmployee() {
        Result<?> actual = postgresProvider.deleteEmployee(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentEmployee[1]: delete nonexistent employee actual {}", actual.getCode());
        logger.debug("deleteNonExistentEmployee[2]: delete nonexistent employee expected {}", ResultCode.NOT_FOUND);
    }

    @Test
    void getTasksByProjectId() {
        tasks.forEach(postgresProvider::processNewTask);

        Result<ArrayList<Task>> result = postgresProvider.getTasksByProjectId(project.getId());
        assertEquals(ResultCode.SUCCESS, result.getCode());

        logger.debug("getTasksByProjectId[1]: actual {}", result.getData());
        logger.debug("getTasksByProjectId[2]: expected {}", tasks);
    }

    @Test
    void getTasksFromProjectWithNoTasks() {
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByProjectId(project.getId());

        logger.debug("getTasksByProjectId[1]: actual {}", actual.getData());
        logger.debug("getTasksByProjectId[2]: expected []");
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Test
    void getTasksFromNonExistentProject() {
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByProjectId("some project");

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
        logger.debug("getTasksFromNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksFromNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);
    }

    @Test
    void getTasksByEmployeeId() {
        processNewTask();
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByEmployeeId(employee.getId());

        logger.debug("getTasksByEmployeeId[1]: expected tasks {}", tasks);
        logger.debug("getTasksByEmployeeId[2]: actual tasks {}", actual.getData());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Test
    void getTasksByNonExistentEmployeeId() {
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByEmployeeId(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getTasksByNonExistentEmployeeId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByNonExistentEmployeeId[2]: expected result code: {}", ResultCode.NOT_FOUND);

        logger.debug("getTasksByNonExistentEmployeeId[3]: actual data: {}", actual.getData());
        logger.debug("getTasksByNonExistentEmployeeId[4]: expected data: []");
    }

    @Test
    void getProjectById() {
        Result<Project> actual = postgresProvider.getProjectById(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(project, actual.getData());

        logger.debug("getProjectById[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectById[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getProjectById[3]: actual data {}", actual.getData());
        logger.debug("getProjectById[4]: expected data {}", project);
    }

    @Test
    void getNonExistentProject() {
        Result<Project> actual = postgresProvider.getProjectById("some project");

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentProject[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentProject[4]: expected data null");
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
    void getTasksByTags() {
        tasks.forEach(postgresProvider::processNewTask);
        ArrayList<String> tags = new ArrayList<>(List.of("Tag1"));
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByTags(tags, project.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Test
    void getTasksByNonExistentTags() {
        tasks.forEach(postgresProvider::processNewTask);
        ArrayList<String> tags = new ArrayList<>(List.of("Tag3"));
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByTags(tags, project.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Test
    void getNonExistentTask() {
        Result<Task> actual = postgresProvider.getTaskById(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentTask[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentTask[3]: actual data null");
    }

    @Test
    void getBugReportsByProjectId() {
        for (BugReport bugReport : bugReports) {
            Result<?> createBugReportsResult = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.SUCCESS, createBugReportsResult.getCode());
        }

        Result<ArrayList<BugReport>> actual = postgresProvider.getBugReportsByProjectId(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReports, actual.getData());

        logger.debug("getBugReportsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getBugReportsByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getBugReportsByProjectId[4]: expected data {}", bugReports);
    }

    @Test
    void getBugReportsByNonExistentProjectId() {
        Result<ArrayList<BugReport>> actual = postgresProvider.getBugReportsByProjectId("some project");
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getBugReportsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getBugReportsByNonExistentProjectId[3]: actual data {}", actual.getData());
        logger.debug("getBugReportsByNonExistentProjectId[4]: expected data []");
    }

    @Test
    void getBugReportById() {
        postgresProvider.processNewBugReport(bugReport);
        Result<BugReport> actual = postgresProvider.getBugReportById(bugReport.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReport, actual.getData());

        logger.debug("getBugReportById[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportById[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getBugReportById[3]: actual data {}", actual.getData());
        logger.debug("getBugReportById[4]: expected data {}", bugReport);
    }

    @Test
    void getNonExistentBugReport() {
        Result<BugReport> actual = postgresProvider.getBugReportById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentBugReport[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentBugReport[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentBugReport[4]: expected data null");
    }

    @Test
    void getEventsByProjectId() {
        processNewEvent();
        Result<ArrayList<Event>> actual = postgresProvider.getEventsByProjectId(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(events, actual.getData());

        logger.debug("getEventsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getEventsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getEventsByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getEventsByProjectId[4]: expected data {}", actual.getData());
    }

    @Test
    void getEventsByNonExistentProjectId() {
        Result<ArrayList<Event>> actual = postgresProvider.getEventsByProjectId("some project");
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getEventsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getEventsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getEventsByNonExistentProjectId[3]: actual data {}", actual.getData());
        logger.debug("getEventsByNonExistentProjectId[4]: expected data []");
    }

    @Test
    void getEventById() {
        postgresProvider.processNewEvent(event);
        Result<Event> actual = postgresProvider.getEventById(event.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(event, actual.getData());

        logger.debug("getEventById[1]: actual result code {}", actual.getCode());
        logger.debug("getEventById[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getEventById[3]: actual data {}", actual.getData());
        logger.debug("getEventById[4]: expected data {}", event);
    }

    @Test
    void getNonExistentEvent() {
        Result<Event> actual = postgresProvider.getEventById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentEvent[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEvent[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentEvent[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentEvent[4]: expected data null");
    }

    @Test
    void getDocumentationsByProjectId() {
        postgresProvider.processNewDocumentation(documentation);
        Result<ArrayList<Documentation>> actual = postgresProvider.getDocumentationsByProjectId(documentation.getProjectId());
        ArrayList<Documentation> expected = new ArrayList<>(Collections.singletonList(documentation));

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(expected, actual.getData());

        logger.debug("getDocumentationByProjectId[2]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getDocumentationByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getDocumentationByProjectId[4]: expected data {}", documentation);
    }

    @Test
    void getDocumentationsByNonExistentProjectId() {
        Result<ArrayList<Documentation>> actual = postgresProvider.getDocumentationsByProjectId(documentation.getProjectId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getDocumentationByProjectId[2]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationByProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getDocumentationByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getDocumentationByProjectId[4]: expected data []");
    }

    @Test
    void getProjectTeam() {
        bindEmployeeToProject();
        Result<ArrayList<Employee>> actual = postgresProvider.getProjectTeam(project.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(team, actual.getData());

        logger.debug("getProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeam[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getProjectTeam[3]: actual data {}", actual.getData());
        logger.debug("getProjectTeam[4]: expected data {}", team);
    }

    @Test
    void getProjectTeamOfNonExistentProject() {
        Result<ArrayList<Employee>> actual = postgresProvider.getProjectTeam("some project");

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getProjectTeamOfNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeamOfNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getProjectTeamOfNonExistentProject[3]: actual data {}", actual.getData());
        logger.debug("getProjectTeamOfNonExistentProject[4]: expected data []");
    }

    @Test
    void getEmptyProjectTeam() {
        Result<ArrayList<Employee>> actual = postgresProvider.getProjectTeam(project.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getEmptyProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getEmptyProjectTeam[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getEmptyProjectTeam[3]: actual data {}", actual.getData());
        logger.debug("getEmptyProjectTeam[4]: expected data []");
    }

    @Test
    void getEmployeeById() {
        postgresProvider.processNewEmployee(employee);
        Result<Employee> actual = postgresProvider.getEmployeeById(employee.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(employee, actual.getData());

        logger.debug("getEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("getEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEmployee[3]: actual {}", actual.getData());
        logger.debug("getEmployee[4]: expected {}", employee);
    }

    @Test
    void getNonExistentEmployee() {
        Result<Employee> actual = postgresProvider.getEmployeeById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentEmployee[3]: actual {}", actual.getData());
        logger.debug("getNonExistentEmployee[4]: expected {}", employee);
    }

    @Test
    void updateEmployee() {
        Employee newEmployee = new Employee(
                "Nikolay",
                "Eremeev",
                LocalDate.of(1999, Month.MAY, 6),
                "Senior mobile dev lead"
        );

        postgresProvider.processNewEmployee(newEmployee);

        newEmployee.setPhoneNumber("7989855668");
        newEmployee.setEmail("example@mail.ru");

        Result<?> actual = postgresProvider.updateEmployee(newEmployee);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("updateEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("updateEmployee[2]: expected result code {}", ResultCode.SUCCESS);
    }

    @Test
    void updateNonExistentEmployee() {
        Employee newEmployee = new Employee(
                "Nikolay",
                "Eremeev",
                LocalDate.of(1999, Month.MAY, 6),
                "Senior mobile dev lead"
        );

        newEmployee.setPhoneNumber("7989855668");
        newEmployee.setEmail("example@mail.ru");

        Result<?> actual = postgresProvider.updateEmployee(newEmployee);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("updateEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("updateEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
    }

    @Test
    void updateTask() {
        Task newTask = new Task(
                "create main page of application",
                "main page of bank application",
                employee.getId(),
                employee.getFullName(),
                project.getId(),
                Priority.MEDIUM
        );

        newTask.completeTask();
        newTask.setComment("some comment");
        newTask.setPriority(Priority.MEDIUM);

        postgresProvider.processNewTask(newTask);
        Result<?> actual = postgresProvider.updateTask(newTask);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("updateTask[1]: actual result code {}", actual.getCode());
        logger.debug("updateTask[2]: expected result code {}", ResultCode.SUCCESS);
    }

    @Test
    void updateNonExistentTask() {
        Task newTask = new Task(
                "create main page of application",
                "main page of bank application",
                employee.getId(),
                employee.getFullName(),
                project.getId(),
                Priority.MEDIUM
        );

        newTask.completeTask();
        newTask.setComment("some comment");
        newTask.setPriority(Priority.MEDIUM);

        Result<?> actual = postgresProvider.updateTask(newTask);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("updateNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("updateNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);
    }

    @Test
    void updateBugReport() {
        BugReport newBugReport = new BugReport(
                "mobile_bank_report_12-05-2023",
                "this is a bug report description",
                employee.getId(),
                employee.getFullName(),
                project.getId(),
                Priority.HIGH
        );

        postgresProvider.processNewBugReport(newBugReport);
        newBugReport.setStatus(BugStatus.CLOSED);
        newBugReport.setPriority(Priority.HIGH);
        newBugReport.setDescription("some description");

        Result<?> actual = postgresProvider.updateBugReport(newBugReport);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("updateBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("updateBugReport[2]: expected result code {}", ResultCode.SUCCESS);
    }
}