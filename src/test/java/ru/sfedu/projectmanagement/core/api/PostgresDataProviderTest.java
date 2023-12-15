package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanagement.core.Queries;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
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
class PostgresDataProviderTest extends BaseProviderTest implements IDataProviderTest {
    private static final PostgresDataProvider postgresProvider = new PostgresDataProvider();
    private static final Logger logger = LogManager.getLogger(PostgresDataProviderTest.class);
    private final Connection connection = postgresProvider.getConnection();


    private void initProjectAndEmployee() {
        postgresProvider.processNewProject(project1);
    }

    @BeforeEach
    void resetDb() throws SQLException {
        Connection connection = postgresProvider.getConnection();
        Statement statement = connection.createStatement();
        String[] queries = {
                Queries.PROJECT_TABLE_NAME,
                Queries.EVENTS_TABLE_NAME,
                Queries.TASKS_TABLE_NAME,
                Queries.EMPLOYEES_TABLE_NAME,
                Queries.BUG_REPORTS_TABLE_NAME,
                Queries.DOCUMENTATIONS_TABLE_NAME,
                Queries.EMPLOYEE_PROJECT_TABLE_NAME
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


    @Override
    @Order(1)
    @Test
    public void processNewProject() {
        try {
            truncateTable(connection.createStatement(), Queries.PROJECT_TABLE_NAME);
            truncateTable(connection.createStatement(), Queries.EMPLOYEES_TABLE_NAME);
        }
        catch (SQLException exception) {
            logger.error("processNewProject[1]: {}", exception.getMessage());
        }

        Result<?> actual = postgresProvider.processNewProject(project1);

        logger.debug("processNewProject[2]: actual result code {}", actual.getCode());
        logger.debug("processNewProject[3]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[4]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

    }

    @Override
    @Order(2)
    @Test
    public void createExistingProject() {
        postgresProvider.processNewProject(project1);
        Result<?> actual = postgresProvider.processNewProject(project1);
        logger.debug("createExistingProject[2]: actual result code {}", actual.getCode());
        logger.debug("createExistingProject[1]: expected result code {}", ResultCode.ERROR);
        logger.debug("createExistingProject[1]: result {}", actual);

        assertEquals(ResultCode.ERROR, actual.getCode());

    }

    @Override
    @Order(3)
    @Test
    public void processNewEmployee() {
        try {
            truncateTable(postgresProvider.getConnection().createStatement(), "employees");
        }
        catch (SQLException exception) {
            logger.error("processNewEmployee[1]: {}", exception.getMessage());
        }

        Result<?> actual = postgresProvider.processNewEmployee(employee1);

        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewEmployee[2]: actual result code {}", actual.getCode());
        logger.debug("processNewEmployee[1]: expected result code {}", ResultCode.SUCCESS);
    }

    @Override
    @Order(4)
    @Test
    public void createExistingEmployee() {
        postgresProvider.processNewEmployee(employee1);
        Result<?> actual = postgresProvider.processNewEmployee(employee1);

        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("createExistingEmployee[2]: actual result code {}", actual.getCode());
        logger.debug("createExistingEmployee[1]: expected result code {}", ResultCode.ERROR);
        logger.debug("createExistingEmployee[1]: result {}", actual);
    }


    @Override
    @Order(5)
    @Test
    public void processNewTask() {
        postgresProvider.processNewProject(project1);
        tasks.forEach(task -> {
            Result<?> actual = postgresProvider.processNewTask(task);
            assertEquals(ResultCode.SUCCESS, actual.getCode());
            int logIndex = 0;

            logger.debug("processNewTask[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("processNewTask[{}]: expected result code {}", ++logIndex, ResultCode.SUCCESS);
            logger.debug("processNewTask[{}]: result {}", ++logIndex, actual);
        });
    }

    @Override
    @Order(6)
    @Test
    public void createExistingTasks() {
        tasks.forEach(task -> {
            postgresProvider.processNewTask(task);
            Result<?> actual = postgresProvider.processNewTask(task);
            assertEquals(ResultCode.ERROR, actual.getCode());
            int logIndex = 0;

            logger.debug("createExistingTasks[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("createExistingTasks[{}]: expected result code {}", ++logIndex, ResultCode.ERROR);
            logger.debug("createExistingTasks[{}]: result {}", ++logIndex, actual);
        });
    }

    @Override
    @Order(7)
    @Test
    public void processNewBugReport() {
        bugReports.forEach(bugReport -> {
            Result<?> actual = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.SUCCESS, actual.getCode());
            int logIndex = 0;

            logger.debug("createExistingBugReports[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("createExistingBugReports[{}]: expected result code {}", ++logIndex, ResultCode.SUCCESS);
            logger.debug("createExistingBugReports[{}]: result {}", ++logIndex, actual);
        });
    }

    @Override
    @Order(8)
    @Test
    public void createExistingBugReports() {
        bugReports.forEach(bugReport -> {
            postgresProvider.processNewBugReport(bugReport);
            Result<?> actual = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.ERROR, actual.getCode());
            int logIndex = 0;

            logger.debug("createExistingBugReports[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("createExistingBugReports[{}]: expected result code {}", ++logIndex, ResultCode.ERROR);
            logger.debug("createExistingBugReports[{}]: result {}", ++logIndex, actual);
        });
    }

    @Override
    @Order(9)
    @Test
    public void processNewDocumentation() {
        Result<?> actual = postgresProvider.processNewDocumentation(documentation);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        logger.debug("processNewDocumentation[1]: actual {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[3]: result {}", actual);
    }

    @Override
    @Order(10)
    @Test
    public void createExistingDocumentation() {
        postgresProvider.processNewDocumentation(documentation);
        Result<?> actual = postgresProvider.processNewDocumentation(documentation);

        assertEquals(ResultCode.ERROR, actual.getCode());
        logger.debug("processNewDocumentation[1]: actual {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected {}", ResultCode.ERROR);
        logger.debug("processNewDocumentation[3]: result {}", actual);
    }

    @Override
    @Order(11)
    @Test
    public void processNewEvent() {
        Result<?> actual = postgresProvider.processNewEvent(event);
        logger.debug("processNewEvent[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewEvent[2]: actual {}", actual.getCode());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Order(12)
    @Test
    public void createExistingEvent() {
        postgresProvider.processNewEvent(event);
        Result<?> actual = postgresProvider.processNewEvent(event);
        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("processNewEvent[1]: actual {}", actual.getCode());
        logger.debug("processNewEvent[2]: expected {}", ResultCode.ERROR);
    }

    @Override
    @Order(13)
    @Test
    public void trackTaskStatus() {
        tasks.forEach(postgresProvider::processNewTask);
        bugReports.forEach(postgresProvider::processNewBugReport);

        TrackInfo<Task, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            for (Task task : tasks) put(task, task.getStatus().name());
        }});

        TrackInfo<Task, String> trackInfoActual = postgresProvider.trackTaskStatus(project1.getId());
        assertEquals(trackInfoExpected, trackInfoActual);

        logger.debug("trackTaskStatus[1]: actual {}", trackInfoActual);
        logger.debug("trackTaskStatus[2]: expected {}", trackInfoExpected);
    }

    @Override
    public void initDataForMonitorProjectCharacteristics() {
        tasks.forEach(postgresProvider::processNewTask);

        postgresProvider.bindEmployeeToProject(employee1.getId(), project1.getId());
        for (Task task : tasks) {
            Result<?> bindEmployeeToTaskResult = postgresProvider.bindTaskExecutor(
                    employee1.getId(), employee1.getFullName(),
                    task.getId(), project1.getId()
            );
            assertEquals(ResultCode.SUCCESS, bindEmployeeToTaskResult.getCode());
        }
    }

    @Override
    @Order(13)
    @Test
    public void monitorProjectCharacteristics() {
        initDataForMonitorProjectCharacteristics();

        float projectReadiness = postgresProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project1.getId());

        ProjectStatistics expectedData = new ProjectStatistics();
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);

        ProjectStatistics actual = postgresProvider.monitorProjectCharacteristics(project1.getId(), false, false);
        assertEquals(expectedData, actual);

        logger.debug("monitorProjectCharacteristics[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristics[2]: actual {}", expectedData);
    }

    @Override
    @Order(14)
    @Test
    public void monitorProjectCharacteristicsWithLaborEfficiency() {
        initDataForMonitorProjectCharacteristics();
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = postgresProvider.calculateLaborEfficiency(project1.getId());

        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);

        ProjectStatistics actual = postgresProvider.monitorProjectCharacteristics(project1.getId(), true, false);
        assertEquals(expectedData, actual);

        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[2]: expected {}", expectedData);
    }

    @Override
    @Order(15)
    @Test
    public void monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency() {
        initDataForMonitorProjectCharacteristics();
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = postgresProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = postgresProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = postgresProvider.calculateLaborEfficiency(project1.getId());

        TrackInfo<BugReport, String> bugStatuses = postgresProvider.trackBugReportStatus(project1.getId());
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);
        expectedData.setBugReportStatus(bugStatuses);

        ProjectStatistics result = postgresProvider.monitorProjectCharacteristics(project1.getId(), true, true);
        assertEquals(expectedData, result);

        logger.debug("monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency[1]: actual {}", result);
        logger.debug("monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency[2]: expected {}", expectedData);
    }

    @Override
    @Order(16)
    @Test
    public void calculateProjectReadiness() {
        ArrayList<Task> tasks1 = new ArrayList<>() {{ addAll(tasks); }};
        int completedTasksCount = (int) tasks1.stream()
                .filter(task -> task.getStatus() == WorkStatus.COMPLETED).count();
        float expectedReadiness = ((float) completedTasksCount / tasks.size()) * 100.0f;
        for (Task task : tasks1) {
            postgresProvider.processNewTask(task);
            postgresProvider.bindTaskExecutor(employee1.getId(), employee1.getFullName(), task.getId(), project1.getId());
        }

        float actualReadiness = postgresProvider.calculateProjectReadiness(project1.getId());
        assertEquals(expectedReadiness, actualReadiness);

        logger.debug("calculateProjectReadiness[1]: actual {}", actualReadiness);
        logger.debug("calculateProjectReadiness[2]: expected {}", expectedReadiness);
    }

    @Override
    @Order(17)
    @Test
    public void calculateProjectReadinessIfHasNoTasks() {
        float actualReadiness = postgresProvider.calculateProjectReadiness(project1.getId());
        float expectedReadiness = 0f;
        assertEquals(expectedReadiness, actualReadiness);

        logger.debug("calculateProjectReadinessIfHasNoTasks[1]: actual project readiness: {}", actualReadiness);
        logger.debug("calculateProjectReadinessIfHasNoTasks[2]: expected project readiness {}", expectedReadiness);
    }

    @Override
    @Order(18)
    @Test
    public void calculateLaborEfficiency() {
        ArrayList<Task> tasks1 = new ArrayList<>() {{addAll(tasks);}};
        tasks1.forEach(task -> task.setStatus(WorkStatus.COMPLETED));

        tasks1.get(0).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 15, 15, 30));
        tasks1.get(1).setCompletedAt(LocalDateTime.of(2023, Month.NOVEMBER, 17, 12,42));
        tasks1.get(2).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 14, 17,24));

        tasks1.forEach(postgresProvider::processNewTask);
//        postgresProvider.processNewProject(project2);

        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee1, 102.0f);}}
        );

        tasks1.forEach(task -> {
            Result<?> bindTaskExecutorResult = postgresProvider.bindTaskExecutor(
                    employee1.getId(), employee1.getFullName(), task.getId(), task.getProjectId()
            );
            assertEquals(ResultCode.SUCCESS, bindTaskExecutorResult.getCode());
        });

        TrackInfo<Employee, Float> actual = postgresProvider.calculateLaborEfficiency(project1.getId());
        assertEquals(expectedData, actual);

        logger.debug("calculateLaborEfficiency[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiency[2]: actual {}", expectedData);
    }

    @Override
    @Order(19)
    @Test
    public void calculateLaborEfficiencyIfEmployeeHasNoTasks() {
        postgresProvider.bindEmployeeToProject(employee1.getId(), project1.getId());
        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee1, 0f);}}
        );

        TrackInfo<Employee, Float> actual = postgresProvider.calculateLaborEfficiency(project1.getId());
        assertEquals(expectedData, actual);

        logger.debug("calculateLaborEfficiencyIfEmployeeHasNoTasks[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiencyIfEmployeeHasNoTasks[2]: actual {}", expectedData);
    }

    @Override
    @Order(20)
    @Test
    public void trackBugReportStatus() {
        TrackInfo<BugReport, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            for (BugReport bugReport : bugReports)
                put(bugReport, bugReport.getStatus().name());
        }});
        processNewBugReport();

        TrackInfo<BugReport, String> trackInfoActual = postgresProvider.trackBugReportStatus(project1.getId());
        logger.debug("processNewProject[1]: expected {}", trackInfoExpected);
        logger.debug("processNewProject[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }


    @Override
    @Test
    public void bindProjectManager() {
        postgresProvider.bindEmployeeToProject(employee1.getId(), project1.getId());
        Result<?> actual = postgresProvider
                .bindProjectManager(employee1.getId(), project1.getId());

        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", actual.getCode());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void bindTaskExecutor() {
        Employee newExecutor = new Employee(
                "Alexey",
                "Zaycev",
                LocalDate.of(1994, Month.FEBRUARY, 1),
                "Senior backend dev"
        );
        postgresProvider.processNewEmployee(newExecutor);
        postgresProvider.bindEmployeeToProject(newExecutor.getId(), project1.getId());
        postgresProvider.processNewTask(task);

        Result<?> actual = postgresProvider.bindTaskExecutor(
                newExecutor.getId(), newExecutor.getFullName(), task.getId(), project1.getId()
        );

        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewProject[1]: expected {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[2]: actual {}", actual.getCode());
    }


    @Override
    @Test
    public void bindEmployeeToProject() {
        for (Employee employee : team) {
            postgresProvider.processNewEmployee(employee);

            Result<?> bindResult = postgresProvider.bindEmployeeToProject(employee.getId(), project1.getId());
            assertEquals(ResultCode.SUCCESS, bindResult.getCode());
        }
    }

    @Override
    @Test
    public void deleteProject() {
        Result<?> actual = postgresProvider.deleteProject(project1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteProject[1]: delete project actual: {}", actual.getCode());
        logger.debug("deleteProject[2]: delete project expected: {}", ResultCode.SUCCESS);
    }

    @Override
    @Test
    public void deleteNonExistentProject() {
        Result<?> actual = postgresProvider.deleteProject(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentProject[1]: delete nonexistent project actual: {}", actual.getCode());
        logger.debug("deleteNonExistentProject[2]: delete nonexistent project expected: {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void deleteTask() {
        postgresProvider.processNewTask(task);

        Result<?> actual = postgresProvider.deleteTask(task.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteTask[1]: delete task actual: {}", actual.getCode());
        logger.debug("deleteTask[2]: delete task expected: {}", ResultCode.SUCCESS);
    }

    @Override
    @Test
    public void deleteNonExistentTask() {
        Result<?> actual = postgresProvider.deleteTask(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentTask[1]: delete nonexistent task actual: {}", actual.getCode());
        logger.debug("deleteNonExistentTask[2]: delete nonexistent task expected: {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void deleteBugReport() {
        postgresProvider.processNewBugReport(bugReport);
        Result<?> actual = postgresProvider.deleteBugReport(bugReport.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteBugReport[1]: delete bug report actual {}", actual.getCode());
        logger.debug("deleteBugReport[2]: delete bug report expected {}", ResultCode.SUCCESS);
    }

    @Override
    @Test
    public void deleteNonExistentBugReport() {
        Result<?> actual = postgresProvider.deleteBugReport(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentBugReport[1]: delete nonexistent bug report actual {}", actual.getCode());
        logger.debug("deleteNonExistentBugReport[2]: delete nonexistent bug report expected {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void deleteEvent() {
        postgresProvider.processNewEvent(event);

        Result<?> actual = postgresProvider.deleteEvent(event.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteBugReport[1]: delete existent event actual {}", actual.getCode());
        logger.debug("deleteBugReport[2]: delete existent event expected {}", ResultCode.SUCCESS);
    }

    @Override
    @Test
    public void deleteNonExistentEvent() {
        Result<?> actual = postgresProvider.deleteEvent(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentEvent[1]: delete nonexistent event actual {}", actual.getCode());
        logger.debug("deleteNonExistentEvent[2]: delete nonexistent event expected {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void deleteDocumentation() {
        postgresProvider.processNewDocumentation(documentation);

        Result<?> actual = postgresProvider.deleteDocumentation(documentation.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteDocumentation[1]: delete documentation actual {}", actual.getCode());
        logger.debug("deleteDocumentation[2]: delete documentation expected {}", ResultCode.SUCCESS);
    }

    @Override
    @Test
    public void deleteNonExistentDocumentation() {
        Result<?> actual = postgresProvider.deleteDocumentation(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentDocumentation[1]: delete nonexistent documentation actual {}", actual.getCode());
        logger.debug("deleteNonExistentBugReport[2]: delete nonexistent documentation expected {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void deleteEmployee() {
        Result<?> actual = postgresProvider.deleteEmployee(employee1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("deleteEmployee[1]: delete employee actual {}", actual.getCode());
        logger.debug("deleteEmployee[2]: delete employee expected {}", ResultCode.SUCCESS);
    }

    @Override
    @Test
    public void deleteNonExistentEmployee() {
        Result<?> actual = postgresProvider.deleteEmployee(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());

        logger.debug("deleteNonExistentEmployee[1]: delete nonexistent employee actual {}", actual.getCode());
        logger.debug("deleteNonExistentEmployee[2]: delete nonexistent employee expected {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void getTasksByProjectId() {
        tasks.forEach(postgresProvider::processNewTask);

        Result<ArrayList<Task>> result = postgresProvider.getTasksByProjectId(project1.getId());
        assertEquals(ResultCode.SUCCESS, result.getCode());

        logger.debug("getTasksByProjectId[1]: actual {}", result.getData());
        logger.debug("getTasksByProjectId[2]: expected {}", tasks);
    }


    @Override
    @Test
    public void getTasksFromProjectWithNoTasks() {
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByProjectId(project1.getId());

        logger.debug("getTasksByProjectId[1]: actual {}", actual.getData());
        logger.debug("getTasksByProjectId[2]: expected []");
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getTasksByNonExistentProjectId() {
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByProjectId(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
        logger.debug("getTasksFromNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksFromNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);
    }

    @Override
    @Test
    public void getTasksByEmployeeId() {
        processNewTask();
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByEmployeeId(employee1.getId());

        logger.debug("getTasksByEmployeeId[1]: expected tasks {}", tasks);
        logger.debug("getTasksByEmployeeId[2]: actual tasks {}", actual.getData());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Override
    @Test
    public void getTasksByNonExistentEmployeeId() {
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByEmployeeId(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getTasksByNonExistentEmployeeId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByNonExistentEmployeeId[2]: expected result code: {}", ResultCode.NOT_FOUND);

        logger.debug("getTasksByNonExistentEmployeeId[3]: actual data: {}", actual.getData());
        logger.debug("getTasksByNonExistentEmployeeId[4]: expected data: []");
    }

    @Override
    @Test
    public void getProjectById() {
        Result<Project> actual = postgresProvider.getProjectById(project1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(project1, actual.getData());

        logger.debug("getProjectById[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectById[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getProjectById[3]: actual data {}", actual.getData());
        logger.debug("getProjectById[4]: expected data {}", project1);
    }

    @Override
    @Test
    public void getNonExistentProject() {
        Result<Project> actual = postgresProvider.getProjectById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentProject[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentProject[4]: expected data null");
    }

    @Override
    @Test
    public void getTaskById() {
        tasks.forEach(postgresProvider::processNewTask);
        for (Task task : tasks) {
            Result<Task> taskResult = postgresProvider.getTaskById(task.getId());
            assertEquals(ResultCode.SUCCESS, taskResult.getCode());
            assertEquals(task, taskResult.getData());
        }
    }

    @Override
    @Test
    public void getTasksByTags() {
        tasks.forEach(postgresProvider::processNewTask);
        ArrayList<String> tags = new ArrayList<>(List.of("Tag1"));
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByTags(tags, project1.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Override
    @Test
    public void getTasksByNonExistentTags() {
        tasks.forEach(postgresProvider::processNewTask);
        ArrayList<String> tags = new ArrayList<>(List.of("Tag3"));
        Result<ArrayList<Task>> actual = postgresProvider.getTasksByTags(tags, project1.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getNonExistentTask() {
        Result<Task> actual = postgresProvider.getTaskById(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentTask[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentTask[3]: actual data null");
    }

    @Override
    @Test
    public void getBugReportsByProjectId() {
        for (BugReport bugReport : bugReports) {
            Result<?> createBugReportsResult = postgresProvider.processNewBugReport(bugReport);
            assertEquals(ResultCode.SUCCESS, createBugReportsResult.getCode());
        }

        Result<ArrayList<BugReport>> actual = postgresProvider.getBugReportsByProjectId(project1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReports, actual.getData());

        logger.debug("getBugReportsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getBugReportsByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getBugReportsByProjectId[4]: expected data {}", bugReports);
    }

    @Override
    @Test
    public void getBugReportsByNonExistentProjectId() {
        Result<ArrayList<BugReport>> actual = postgresProvider.getBugReportsByProjectId(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getBugReportsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getBugReportsByNonExistentProjectId[3]: actual data {}", actual.getData());
        logger.debug("getBugReportsByNonExistentProjectId[4]: expected data []");
    }

    @Override
    @Test
    public void getBugReportById() {
        postgresProvider.processNewBugReport(bugReport);
        Result<BugReport> actual = postgresProvider.getBugReportById(bugReport.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReport, actual.getData());

        logger.debug("getBugReportById[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportById[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getBugReportById[3]: actual data {}", actual.getData());
        logger.debug("getBugReportById[4]: expected data {}", bugReport);
    }

    @Override
    @Test
    public void getNonExistentBugReport() {
        Result<BugReport> actual = postgresProvider.getBugReportById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentBugReport[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentBugReport[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentBugReport[4]: expected data null");
    }

    @Override
    @Test
    public void getEventsByProjectId() {
        processNewEvent();
        Result<ArrayList<Event>> actual = postgresProvider.getEventsByProjectId(project1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(events, actual.getData());

        logger.debug("getEventsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getEventsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getEventsByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getEventsByProjectId[4]: expected data {}", actual.getData());
    }

    @Override
    @Test
    public void getEventsByNonExistentProjectId() {
        Result<ArrayList<Event>> actual = postgresProvider.getEventsByProjectId(UUID.randomUUID());
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getEventsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getEventsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getEventsByNonExistentProjectId[3]: actual data {}", actual.getData());
        logger.debug("getEventsByNonExistentProjectId[4]: expected data []");
    }

    @Override
    @Test
    public void getEventById() {
        postgresProvider.processNewEvent(event);
        Result<Event> actual = postgresProvider.getEventById(event.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(event, actual.getData());

        logger.debug("getEventById[1]: actual result code {}", actual.getCode());
        logger.debug("getEventById[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getEventById[3]: actual data {}", actual.getData());
        logger.debug("getEventById[4]: expected data {}", event);
    }

    @Override
    @Test
    public void getNonExistentEvent() {
        Result<Event> actual = postgresProvider.getEventById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentEvent[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEvent[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getNonExistentEvent[3]: actual data {}", actual.getData());
        logger.debug("getNonExistentEvent[4]: expected data null");
    }

    @Override
    @Test
    public void getDocumentationsByProjectId() {
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

    @Override
    @Test
    public void getDocumentationById() {
        postgresProvider.processNewDocumentation(documentation);
        Result<Documentation> actual = postgresProvider.getDocumentationById(documentation.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(documentation, actual.getData());

        logger.debug("getDocumentationById[1]: actual result code {}", ResultCode.SUCCESS);
        logger.debug("getDocumentationById[2]: expected result code {}", actual.getCode());
        logger.debug("getDocumentationById[3]: result {}", actual.getData());
    }

    @Override
    @Test
    public void getNonExistentDocumentation() {

    }

    @Override
    @Test
    public void getDocumentationsByNonExistentProjectId() {
        Result<ArrayList<Documentation>> actual = postgresProvider.getDocumentationsByProjectId(documentation.getProjectId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getDocumentationByProjectId[2]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationByProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getDocumentationByProjectId[3]: actual data {}", actual.getData());
        logger.debug("getDocumentationByProjectId[4]: expected data []");
    }

    @Override
    @Test
    public void getProjectTeam() {
        Result<ArrayList<Employee>> actual = postgresProvider.getProjectTeam(project1.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(List.of(employee1)), actual.getData());

        logger.debug("getProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeam[2]: expected result code {}", ResultCode.SUCCESS);

        logger.debug("getProjectTeam[3]: actual data {}", actual.getData());
        logger.debug("getProjectTeam[4]: expected data {}", team);
    }

    @Override
    @Test
    public void getProjectTeamOfNonExistentProject() {
        Result<ArrayList<Employee>> actual = postgresProvider.getProjectTeam(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getProjectTeamOfNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeamOfNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getProjectTeamOfNonExistentProject[3]: actual data {}", actual.getData());
        logger.debug("getProjectTeamOfNonExistentProject[4]: expected data []");
    }

    @Override
    @Test
    public void getEmptyProjectTeam() {
        Project project3 = createProject(
                UUID.randomUUID(),
                "name",
                "description",
                WorkStatus.IN_PROGRESS,
                LocalDateTime.of(2023, Month.DECEMBER, 12, 15, 15),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );
        Result<ArrayList<Employee>> actual = postgresProvider.getProjectTeam(project3.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());

        logger.debug("getEmptyProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getEmptyProjectTeam[2]: expected result code {}", ResultCode.NOT_FOUND);

        logger.debug("getEmptyProjectTeam[3]: actual data {}", actual.getData());
        logger.debug("getEmptyProjectTeam[4]: expected data []");
    }

    @Override
    @Test
    public void getEmployeeById() {
        postgresProvider.processNewEmployee(employee1);
        Result<Employee> actual = postgresProvider.getEmployeeById(employee1.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(employee1, actual.getData());

        logger.debug("getEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("getEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEmployee[3]: actual {}", actual.getData());
        logger.debug("getEmployee[4]: expected {}", employee1);
    }

    @Override
    @Test
    public void getNonExistentEmployee() {
        Result<Employee> actual = postgresProvider.getEmployeeById(UUID.randomUUID());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());

        logger.debug("getNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentEmployee[3]: actual {}", actual.getData());
        logger.debug("getNonExistentEmployee[4]: expected {}", employee1);
    }
}