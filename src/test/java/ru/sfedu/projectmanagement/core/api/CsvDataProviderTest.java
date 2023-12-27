package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.csv.CsvUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CsvDataProviderTest extends BaseProviderTest implements IDataProviderTest {
    private static final Logger logger = LogManager.getLogger(CsvDataProviderTest.class);
    private final CsvDataProvider csvProvider = new CsvDataProvider();

    @BeforeEach
    void deleteFiles() {
        String actualDatasourcePath = Constants.DATASOURCE_TEST_PATH_CSV;

        String projectsFilePath = actualDatasourcePath
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String employeesFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEES_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String tasksFilePath = actualDatasourcePath
                .concat(Constants.TASKS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String bugReportsFilePath = actualDatasourcePath
                .concat(Constants.BUG_REPORTS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String eventsFilePath = actualDatasourcePath
                .concat(Constants.EVENTS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String documentationsFilePath = actualDatasourcePath
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String employeeProjectFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String taskTagsFilePath = actualDatasourcePath
                .concat(Constants.TASK_TAG_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String documentationDataFilePath = actualDatasourcePath
                .concat(Constants.DOCUMENTATION_DATA_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        String managerProjectFilePath = actualDatasourcePath
                .concat(Constants.MANAGER_PROJECT_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);


//        HashMap<String, Class<? extends Entity>> list = new HashMap<>() {{
//            put(projectsFilePath, Project.class);
//            put(employeesFilePath, Employee.class);
//            put(employeeProjectFilePath, EmployeeProjectObject.class);
//            put(tasksFilePath, Task.class);
//            put(bugReportsFilePath, BugReport.class);
//            put(eventsFilePath, Event.class);
//            put(documentationsFilePath, Documentation.class);
//            put(taskTagsFilePath, TaskTag.class);
//            put(documentationDataFilePath, DocumentationData.class);
//            put(managerProjectFilePath, ManagerProjectObject.class);
//        }};

        List<String> files = List.of(
                projectsFilePath, employeesFilePath, employeeProjectFilePath,
                tasksFilePath, bugReportsFilePath, eventsFilePath,
                documentationsFilePath, documentationDataFilePath,
                taskTagsFilePath, managerProjectFilePath
        );

        files.forEach(CsvUtil::truncateFile);
        project1.setTasks(new ArrayList<>());
        project1.setBugReports(new ArrayList<>());
        project1.setEvents(new ArrayList<>());
    }

    @Override
    @Test
    public void processNewProject() {
        Result<NoData> actual = csvProvider.processNewProject(project1);

        logger.debug("processNewProject[1]: actual result code {}", actual.getCode());
        logger.debug("processNewProject[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void processExistingProject() {
        csvProvider.processNewProject(project1);
        Result<NoData> actual = csvProvider.processNewProject(project1);

        logger.debug("createExistingProject[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingProject[2]: expected result code {}", ResultCode.ERROR);
        logger.debug("createExistingProject[3]: result {}", actual);

        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Test
    public void processNewTasks() {
        Result<NoData> projectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        tasks.forEach(task -> {
            Result<NoData> actual = csvProvider.processNewTask(task);
            logger.debug("processNewTask[1]: actual result code {}", actual.getCode());
            logger.debug("processNewTask[2]: expected result code {}", ResultCode.SUCCESS);
            logger.debug("processNewTask[3]: result {}", actual);

            assertEquals(ResultCode.SUCCESS, actual.getCode());
        });
    }

    @Override
    @Test
    public void processExistingTasks() {
        Project project = new Project(project1);
        project.setTasks(tasks);
        Result<NoData> projectResult = csvProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        tasks.forEach(task -> {
            Result<NoData> actual = csvProvider.processNewTask(task);
            logger.debug("processNewTask[1]: actual result code {}", actual.getCode());
            logger.debug("processNewTask[2]: expected result code {}", ResultCode.SUCCESS);
            logger.debug("processNewTask[3]: result {}", actual);

            assertEquals(ResultCode.ERROR, actual.getCode());
        });
    }

    @Override
    @Test
    public void processNewBugReports() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        logger.debug("processNewBugReports[1]: resutl {}", createProjectResult);

        bugReports.forEach(bugReport -> {
            Result<NoData> actual = csvProvider.processNewBugReport(bugReport);

            logger.debug("processNewBugReport[1]: actual result code {}", actual.getCode());
            logger.debug("processNewBugReport[2]: expected result code {}", ResultCode.SUCCESS);
            logger.debug("processNewBugReport[3]: result {}", actual);

            assertEquals(ResultCode.SUCCESS, actual.getCode());
        });
    }

    @Override
    @Test
    public void processExistingBugReports() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        logger.debug("processExistingBugReports[1]: result {}", createProjectResult.getCode());
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> createResult = csvProvider.processNewBugReport(bugReport);
        assertEquals(ResultCode.SUCCESS, createResult.getCode());

        Result<NoData> actual = csvProvider.processNewBugReport(bugReport);

        logger.debug("createExistingBugReports[2]: actual result code {}", actual.getCode());
        logger.debug("createExistingBugReports[3]: expected result code {}", ResultCode.ERROR);
        logger.debug("createExistingBugReports[4]: result {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Test
    public void processNewDocumentation() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> actual = csvProvider.processNewDocumentation(documentation);

        logger.debug("processNewDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void processExistingDocumentation() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> createDocResult = csvProvider.processNewDocumentation(documentation);
        assertEquals(ResultCode.SUCCESS, createDocResult.getCode());

        Result<NoData> actual = csvProvider.processNewDocumentation(documentation);

        logger.debug("processNewDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected result code {}", ResultCode.ERROR);
        logger.debug("processNewDocumentation[3]: result {}", actual);

        assertEquals(ResultCode.ERROR, actual.getCode());

    }

    @Override
    @Test
    public void processNewEvent() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> actual = csvProvider.processNewEvent(event);

        logger.debug("processNewEvent[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEvent[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewEvent[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void processExistingEvent() {
        project1.setEvents(new ArrayList<>());
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> eventResult = csvProvider.processNewEvent(event);
        logger.debug("createExistingEvent[1]: result {}", eventResult);
        assertEquals(ResultCode.SUCCESS, eventResult.getCode());

        Result<NoData> actual = csvProvider.processNewEvent(event);

        logger.debug("createExistingEvent[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingEvent[2]: expected result code {}", ResultCode.ERROR);
        logger.debug("createExistingEvent[3]: result {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Test
    public void processNewEmployee() {
        Result<NoData> actual = csvProvider.processNewEmployee(employee1);

        logger.debug("processNewEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewEmployee[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }


    @Override
    @Test
    public void processExistingEmployee() {
        Result<NoData> employeeResult = csvProvider.processNewEmployee(employee1);
        assertEquals(ResultCode.SUCCESS, employeeResult.getCode());

        Result<NoData> actual = csvProvider.processNewEmployee(employee1);

        logger.debug("processNewEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewEmployee[3]: result {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Test
    public void trackTaskStatusForNonExistentProject() {
        TrackInfo<Task, String> trackInfoExpected = new TrackInfo<>();

        TrackInfo<Task, String> trackInfoActual = csvProvider.trackTaskStatus(project1.getId());

        logger.debug("trackTaskStatus[1]: actual {}", trackInfoActual);
        logger.debug("trackTaskStatus[2]: expected {}", trackInfoExpected);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    @Override
    @Test
    public void monitorProjectCharacteristics() {
        initDataForMonitorProjectCharacteristics(csvProvider);

        float projectReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = csvProvider.trackTaskStatus(project1.getId());

        ProjectStatistics expectedData = new ProjectStatistics();
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);

        ProjectStatistics actual = csvProvider.monitorProjectCharacteristics(project1.getId(), false, false);

        logger.debug("monitorProjectCharacteristics[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristics[2]: actual {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void monitorNonExistentProjectCharacteristics() {
        float projectReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = csvProvider.trackTaskStatus(project1.getId());

        ProjectStatistics expectedData = new ProjectStatistics();
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);

        ProjectStatistics actual = csvProvider.monitorProjectCharacteristics(project1.getId(), false, false);

        logger.debug("monitorNonExistentProjectCharacteristics[1]: actual {}", actual);
        logger.debug("monitorNonExistentProjectCharacteristics[2]: actual {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency() {
        initDataForMonitorProjectCharacteristics(csvProvider);
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = csvProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = csvProvider.calculateLaborEfficiency(project1.getId());

        TrackInfo<BugReport, String> bugStatuses = csvProvider.trackBugReportStatus(project1.getId());
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);
        expectedData.setBugReportStatus(bugStatuses);

        ProjectStatistics result = csvProvider.monitorProjectCharacteristics(project1.getId(), true, true);

        logger.debug("monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency[1]: actual {}", result);
        logger.debug("monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency[2]: expected {}", expectedData);
        assertEquals(expectedData, result);
    }

    @Override
    @Test
    public void monitorNonExistentProjectCharacteristicsWithBugStatusAndLaborEfficiency() {
        ProjectStatistics expectedData = new ProjectStatistics();
        UUID id = UUID.randomUUID();

        float projectReadiness = csvProvider.calculateProjectReadiness(id);
        TrackInfo<Task, String> trackTasks = csvProvider.trackTaskStatus(id);

        TrackInfo<Employee, Float> laborEfficiency = csvProvider.calculateLaborEfficiency(id);

        TrackInfo<BugReport, String> bugStatuses = csvProvider.trackBugReportStatus(id);
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);
        expectedData.setBugReportStatus(bugStatuses);

        ProjectStatistics result = csvProvider.monitorProjectCharacteristics(id, true, true);

        logger.debug("monitorNonExistentProjectCharacteristicsWithBugStatusAndLaborEfficiency[1]: actual {}", result);
        logger.debug("monitorNonExistentProjectCharacteristicsWithBugStatusAndLaborEfficiency[2]: expected {}", expectedData);
        assertEquals(expectedData, result);
    }

    @Override
    @Test
    public void monitorNonExistentProjectCharacteristicsWithLaborEfficiency() {
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = csvProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = csvProvider.calculateLaborEfficiency(project1.getId());

        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);

        ProjectStatistics actual = csvProvider.monitorProjectCharacteristics(project1.getId(), true, false);

        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[2]: expected {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void monitorProjectCharacteristicsWithLaborEfficiency() {
        initDataForMonitorProjectCharacteristics(csvProvider);
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = csvProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = csvProvider.calculateLaborEfficiency(project1.getId());

        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);

        ProjectStatistics actual = csvProvider.monitorProjectCharacteristics(project1.getId(), true, false);

        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[2]: expected {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void calculateProjectReadiness() {
        project1.setTasks(new ArrayList<>());
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        ArrayList<Task> tasks1 = new ArrayList<>() {{ addAll(tasks); }};
        tasks1.forEach(task -> task.setStatus(WorkStatus.COMPLETED));

        int completedTasksCount = (int) tasks1.stream()
                .filter(task -> task.getStatus() == WorkStatus.COMPLETED).count();
        float expectedReadiness = ((float) completedTasksCount / tasks.size()) * 100.0f;
        tasks1.forEach(csvProvider::processNewTask);

        float actualReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        logger.debug("calculateProjectReadiness[1]: actual {}", actualReadiness);
        logger.debug("calculateProjectReadiness[2]: expected {}", expectedReadiness);

        assertEquals(expectedReadiness, actualReadiness);
    }

    @Override
    @Test
    public void calculateNonExistentProjectReadiness() {
        UUID id = UUID.randomUUID();
        float expectedReadiness = 0f;

        float actualReadiness = csvProvider.calculateProjectReadiness(id);

        logger.debug("calculateNonExistentProjectReadiness[1]: actual {}", actualReadiness);
        logger.debug("calculateNonExistentProjectReadiness[2]: expected {}", expectedReadiness);
        assertEquals(expectedReadiness, actualReadiness);
    }

    @Override
    @Test
    public void calculateProjectReadinessIfHasNoTasks() {
        float actualReadiness = csvProvider.calculateProjectReadiness(project1.getId());
        float expectedReadiness = 0f;

        logger.debug("calculateProjectReadinessIfHasNoTasks[1]: actual project readiness: {}", actualReadiness);
        logger.debug("calculateProjectReadinessIfHasNoTasks[2]: expected project readiness {}", expectedReadiness);
        assertEquals(expectedReadiness, actualReadiness);
    }

    @Override
    @Test
    public void calculateLaborEfficiency() {
        Project project = new Project(project1);
        ArrayList<Task> tasks1 = new ArrayList<>() {{addAll(tasks);}};
        tasks1.forEach(task -> task.setStatus(WorkStatus.COMPLETED));
        project.setTasks(tasks1);

        tasks1.get(0).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 15, 15, 30));
        tasks1.get(1).setCompletedAt(LocalDateTime.of(2023, Month.NOVEMBER, 17, 12,42));
        tasks1.get(2).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 14, 17,24));

        csvProvider.processNewProject(project);


        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee1, 102.0f);}}
        );

        TrackInfo<Employee, Float> actual = csvProvider.calculateLaborEfficiency(project1.getId());

        logger.debug("calculateLaborEfficiency[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiency[2]: actual {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void calculateLaborEfficiencyIfEmployeeHasNoTasks() {
        Project project = new Project(project1);
        project.setTasks(new ArrayList<>());

        csvProvider.processNewProject(project);
        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee1, 0f);}}
        );

        TrackInfo<Employee, Float> actual = csvProvider.calculateLaborEfficiency(project1.getId());

        logger.debug("calculateLaborEfficiencyIfEmployeeHasNoTasks[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiencyIfEmployeeHasNoTasks[2]: actual {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void calculateLaborEfficiencyForNonExistentProject() {
        TrackInfo<Employee, Float> expectedData = new TrackInfo<>();
        TrackInfo<Employee, Float> actual = csvProvider.calculateLaborEfficiency(project1.getId());

        logger.debug("calculateLaborEfficiencyForNonExistentProject[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiencyForNonExistentProject[2]: actual {}", expectedData);
        assertEquals(expectedData, actual);
    }

    @Override
    @Test
    public void trackTaskStatus() {
        csvProvider.processNewProject(project1);
        tasks.forEach(csvProvider::processNewTask);
        bugReports.forEach(csvProvider::processNewBugReport);

        TrackInfo<Task, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            for (Task task : tasks) put(task, task.getStatus().name());
        }});

        TrackInfo<Task, String> trackInfoActual = csvProvider.trackTaskStatus(project1.getId());

        logger.debug("trackTaskStatus[1]: actual {}", trackInfoActual);
        logger.debug("trackTaskStatus[2]: expected {}", trackInfoExpected);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    @Override
    @Test
    public void trackBugReportStatus() {
        Result<NoData> projectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        bugReports.forEach(csvProvider::processNewBugReport);

        TrackInfo<BugReport, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            bugReports.forEach(bugReport -> put(bugReport, bugReport.getStatus().name()));
        }});

        TrackInfo<BugReport, String> trackInfoActual = csvProvider.trackBugReportStatus(project1.getId());
        logger.debug("processNewProject[1]: expected {}", trackInfoExpected);
        logger.debug("processNewProject[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    @Override
    @Test
    public void bindEmployeeToProject() {
        Result<NoData> employeeResult = csvProvider.processNewEmployee(employee2);
        assertEquals(ResultCode.SUCCESS, employeeResult.getCode());

        Result<NoData> projectResult = csvProvider.processNewProject(project2);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<NoData> actual = csvProvider.bindEmployeeToProject(employee2.getId(), project2.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void bindNonExistentEmployeeToProject() {
        team.forEach(employee -> {
            Result<NoData> actual = csvProvider.bindEmployeeToProject(employee.getId(), project1.getId());

            logger.debug("bindEmployeeToProject[1]: actual result code {}", actual.getCode());
            logger.debug("bindEmployeeToProject[2]: expected result code {}", ResultCode.ERROR);
            logger.debug("bindEmployeeToProject[3]: result {}", actual);
            assertEquals(ResultCode.ERROR, actual.getCode());
        });
    }

    @Override
    @Test
    public void trackBugReportStatusForNonExistentProject() {
        TrackInfo<BugReport, String> trackInfoExpected = new TrackInfo<>();

        TrackInfo<BugReport, String> trackInfoActual = csvProvider.trackBugReportStatus(project1.getId());
        logger.debug("processNewProject[1]: expected {}", trackInfoExpected);
        logger.debug("processNewProject[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    @Override
    @Test
    public void bindProjectManager() {
        Project project = new Project(project1);
        Result<NoData> projectResult = csvProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<NoData> createEmployeeResult = csvProvider.processNewEmployee(employee2);
        assertEquals(ResultCode.SUCCESS, createEmployeeResult.getCode());

        Result<NoData> actual = csvProvider.bindProjectManager(employee2.getId(), project.getId());
        logger.debug("bindProjectManager[1]: actual result code {}", actual.getCode());
        logger.debug("bindProjectManager[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("bindProjectManager[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void bindNonExistentProjectManager() {
        Result<NoData> actual = csvProvider
                .bindProjectManager(employee1.getId(), project1.getId());

        logger.debug("bindNonExistentProjectManager[1]: actual result code {}", actual.getCode());
        logger.debug("bindNonExistentProjectManager[2]: expected result code {}", ResultCode.ERROR);
        logger.debug("bindNonExistentProjectManager[3]: result {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Test
    public void deleteProject() {
        Result<NoData> projectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());
        Result<NoData> actual = csvProvider.deleteProject(project1.getId());

        logger.debug("deleteProject[1]: actual result code {}", actual.getCode());
        logger.debug("deleteProject[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteProject[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentProject() {
        Result<NoData> actual = csvProvider.deleteProject(project1.getId());

        logger.debug("deleteNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentProject[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteTask() {
        csvProvider.processNewProject(project1);
        csvProvider.processNewTask(task);
        Result<NoData> actual = csvProvider.deleteTask(task.getId());

        logger.debug("deleteTask[1]: actual result code {}", actual.getCode());
        logger.debug("deleteTask[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteTask[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentTask() {
        Result<NoData> actual = csvProvider.deleteTask(task.getId());

        logger.debug("deleteNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentTask[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteBugReport() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        logger.debug("deleteBugReport[1]: result {}", createProjectResult.getCode());
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> createBugReportResult = csvProvider.processNewBugReport(bugReport);
        logger.debug("deleteBugReport[2]: result {}", createBugReportResult);
        assertEquals(ResultCode.SUCCESS, createBugReportResult.getCode());

        Result<NoData> actual = csvProvider.deleteBugReport(bugReport.getId());

        logger.debug("deleteBugReport[3]: actual result code {}", actual.getCode());
        logger.debug("deleteBugReport[4]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteBugReport[5]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentBugReport() {
        Result<NoData> actual = csvProvider.deleteBugReport(bugReport.getId());

        logger.debug("deleteBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("deleteBugReport[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteBugReport[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteEvent() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> createEventResult = csvProvider.processNewEvent(event);
        assertEquals(ResultCode.SUCCESS, createEventResult.getCode());

        Result<NoData> actual = csvProvider.deleteEvent(event.getId());

        logger.debug("deleteEvent[1]: actual result code {}", actual.getCode());
        logger.debug("deleteEvent[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteEvent[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentEvent() {
        Result<NoData> actual = csvProvider.deleteEvent(event.getId());

        logger.debug("deleteNonExistentEvent[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentEvent[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentEvent[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteDocumentation() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        csvProvider.processNewDocumentation(documentation);
        Result<NoData> actual = csvProvider.deleteDocumentation(documentation.getId());

        logger.debug("deleteDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("deleteDocumentation[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteDocumentation[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentDocumentation() {
        Result<NoData> actual = csvProvider.deleteDocumentation(documentation.getId());

        logger.debug("deleteNonExistentDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentDocumentation[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentDocumentation[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteEmployee() {
        csvProvider.processNewEmployee(employee1);
        Result<NoData> actual = csvProvider.deleteEmployee(employee1.getId());

        logger.debug("deleteEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("deleteEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteEmployee[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentEmployee() {
        Result<NoData> actual = csvProvider.deleteEmployee(employee1.getId());

        logger.debug("deleteNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentEmployee[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void getProjectById() {
        Project project3 = new Project(project1);
        project3.addBugReport(bugReport);
        project3.addEvent(event);
        project3.addTask(task);

        Result<NoData> createProjectResult = csvProvider.processNewProject(project3);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<Project> actual = csvProvider.getProjectById(project3.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(project3, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentProject() {
        Result<Project> actual = csvProvider.getProjectById(project1.getId());

        logger.debug("getNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentProject[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getTasksByTags() {
        ArrayList<Task> expected = new ArrayList<>(tasks);
        Project project3 = new Project(project1);
        project3.setTasks(tasks);

        Result<NoData> projectResult = csvProvider.processNewProject(project3);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());
        Result<List<Task>> actual = csvProvider.getTasksByTags(new ArrayList<>(){{add("Tag1");}}, project3.getId());

        logger.debug("getTasksByTags[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByTags[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByTags[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(expected, actual.getData());
    }

    @Override
    @Test
    public void getTasksByNonExistentTags() {
        ArrayList<Task> expected = new ArrayList<>();
        Project project3 = new Project(project1);

        Result<NoData> projectResult = csvProvider.processNewProject(project3);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());
        Result<List<Task>> actual = csvProvider.getTasksByTags(new ArrayList<>(){{add("Tag14545");}}, project3.getId());

        logger.debug("getTasksByNonExistentTags[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByNonExistentTags[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getTasksByNonExistentTags[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(expected, actual.getData());
    }

    @Override
    @Test
    public void getTasksByProjectId() {
        Project project3 = createProject(
                project1.getId(),
                "project name",
                "project description",
                WorkStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.MAY, 14, 0, 0),
                new ArrayList<>(),
                new ArrayList<>(){{add(bugReport);}},
                new ArrayList<>(){{add(event);}},
                new ArrayList<>(),
                new ArrayList<>(){{add(employee1);}},
                employee1
        );

        Task task1 = createTask(
                project3.getId(),
                employee1.getId(),
                employee1.getFullName(),
                "taskname",
                "task description",
                "comment",
                WorkStatus.IN_PROGRESS,
                new ArrayList<>(){{add("Tag1");}},
                null,
                LocalDateTime.of(2023, Month.DECEMBER, 31, 15, 0),
                Priority.HIGH
        );

        project3.addTask(task1);

        Result<NoData> projectResult = csvProvider.processNewProject(project3);
        logger.debug("getTasksByProjectId[1]: result {}", projectResult);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<List<Task>> actual = csvProvider.getTasksByProjectId(project3.getId());
        ArrayList<ProjectEntity> expected = new ArrayList<>() {{
            addAll(project3.getTasks());
        }};

        logger.debug("getTasksByProjectId[2]: actual result code {}", actual.getCode());
        logger.debug("getTasksByProjectId[3]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByProjectId[4]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(expected, actual.getData());
    }

    @Override
    @Test
    public void getTasksFromProjectWithNoTasks() {
        Project project = new Project(project1);
        project.setTasks(new ArrayList<>());
        Result<NoData> projectResult = csvProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<List<Task>> actual = csvProvider.getTasksByProjectId(project.getId());

        logger.debug("getTasksFromProjectWithNoTasks[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksFromProjectWithNoTasks[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksFromProjectWithNoTasks[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getTasksFromNonExistentProject() {
        Result<List<Task>> actual = csvProvider.getTasksByProjectId(project1.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getTasksByEmployeeId() {
        csvProvider.processNewProject(project1);
        Result<List<Task>> actual = csvProvider.getTasksByEmployeeId(employee1.getId());

        logger.debug("getTasksByEmployeeId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByEmployeeId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByEmployeeId[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(project1.getTasks(), actual.getData());
    }

    @Override
    @Test
    public void getTasksByNonExistentEmployeeId() {
        csvProvider.processNewProject(project1);
        Result<List<Task>> actual = csvProvider.getTasksByEmployeeId(employee2.getId());

        logger.debug("getTasksByNonExistentEmployeeId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByNonExistentEmployeeId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByNonExistentEmployeeId[3]: result {}", actual);

        assertEquals(ResultCode.ERROR, actual.getCode());
        assertEquals(new ArrayList<>(), actual.getData());
    }

    @Override
    @Test
    public void getTaskById() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> taskResult = csvProvider.processNewTask(task);
        assertEquals(ResultCode.SUCCESS, taskResult.getCode());

        Result<Task> actual = csvProvider.getTaskById(task.getId());

        logger.debug("getTaskById[1]: actual result code {}", actual.getCode());
        logger.debug("getTaskById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTaskById[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(task, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentTask() {
        Result<Task> actual = csvProvider.getTaskById(task.getId());

        logger.debug("getNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentTask[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getBugReportsByProjectId() {
        Project project = new Project(project1);
        project.addBugReport(bugReport);

        Result<NoData> createProjectResult = csvProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<List<BugReport>> actual = csvProvider.getBugReportsByProjectId(project.getId());

        logger.debug("getBugReportsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getBugReportsByProjectId[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(){{add(bugReport);}}, actual.getData());
    }

    @Override
    @Test
    public void getBugReportsByNonExistentProjectId() {
        Result<List<BugReport>> actual = csvProvider.getBugReportsByProjectId(project1.getId());

        logger.debug("getBugReportsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getBugReportsByProjectId[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getBugReportById() {
        Project project = new Project(project1);
        project.addBugReport(bugReport);

        Result<NoData> createProjectResult = csvProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<BugReport> actual = csvProvider.getBugReportById(bugReport.getId());

        logger.debug("getBugReportById[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getBugReportById[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReport, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentBugReport() {
        project1.setBugReports(new ArrayList<>());
        Result<NoData> projectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<BugReport> actual = csvProvider.getBugReportById(bugReport.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getEventsByProjectId() {
        ArrayList<Event> expected = new ArrayList<>() {{add(event);}};
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> createEventResult = csvProvider.processNewEvent(event);
        assertEquals(ResultCode.SUCCESS, createEventResult.getCode());

        Result<List<Event>> actual = csvProvider.getEventsByProjectId(project1.getId());

        logger.debug("getEventById[2]: actual result code {}", actual.getCode());
        logger.debug("getEventById[3]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEventById[4]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(expected, actual.getData());

    }

    @Override
    @Test
    public void getEventsByNonExistentProjectId() {
        Result<List<Event>> actual = csvProvider.getEventsByProjectId(project1.getId());

        logger.debug("getEventById[1]: actual result code {}", actual.getCode());
        logger.debug("getEventById[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getEventById[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(new ArrayList<>(), actual.getData());
    }

    @Override
    @Test
    public void getEventById() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<NoData> eventResult = csvProvider.processNewEvent(event);
        assertEquals(ResultCode.SUCCESS, eventResult.getCode());

        Result<Event> actual = csvProvider.getEventById(event.getId());

        logger.debug("getEventById[1]: actual result code {}", actual.getCode());
        logger.debug("getEventById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEventById[3]: result {}", actual);


        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(event, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentEvent() {
        Result<Event> actual = csvProvider.getEventById(event.getId());

        logger.debug("getNonExistentEvent[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEvent[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentEvent[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getDocumentationsByProjectId() {
        Result<NoData> projectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<NoData> docResult = csvProvider.processNewDocumentation(documentation);
        assertEquals(ResultCode.SUCCESS, docResult.getCode());

        Result<List<Documentation>> actual = csvProvider.getDocumentationsByProjectId(project1.getId());

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(List.of(documentation)), actual.getData());
    }

    @Override
    @Test
    public void getDocumentationsByNonExistentProjectId() {
        Result<List<Documentation>> actual = csvProvider.getDocumentationsByProjectId(project1.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getDocumentationById() {
        csvProvider.processNewProject(project1);
        csvProvider.processNewDocumentation(documentation);

        Result<Documentation> actual = csvProvider.getDocumentationById(documentation.getId());

        logger.debug("getDocumentationById[1]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getDocumentationById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(documentation, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentDocumentation() {
        Result<Documentation> actual = csvProvider.getDocumentationById(documentation.getId());

        logger.debug("getNonExistentDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentDocumentation[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentDocumentation[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getProjectTeam() {
        Result<NoData> createProjectResult = csvProvider.processNewProject(project1);
        assertEquals(ResultCode.SUCCESS, createProjectResult.getCode());

        Result<List<Employee>> actual = csvProvider.getProjectTeam(project1.getId());

        logger.debug("getProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeam[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getProjectTeam[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(){{add(employee1);}}, actual.getData());
    }

    @Override
    @Test
    public void getTeamOfNonExistentProject() {
        Result<List<Employee>> actual = csvProvider.getProjectTeam(project2.getId());

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getEmptyProjectTeam() {
        Project project = new Project(project1);
        project.setTeam(new ArrayList<>());
        Result<NoData> projectResult = csvProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, projectResult.getCode());

        Result<List<Employee>> actual = csvProvider.getProjectTeam(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getEmployeeById() {
        csvProvider.processNewEmployee(employee1);
        logger.debug("employee {}", employee1);

        Result<Employee> actual = csvProvider.getEmployeeById(employee1.getId());

        logger.debug("getEmployeeById[1]: actual result code {}", actual.getCode());
        logger.debug("getEmployeeById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEmployeeById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(employee1, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentEmployee() {
        Result<Employee> actual = csvProvider.getEmployeeById(employee2.getId());

        logger.debug("getNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentEmployee[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }
}