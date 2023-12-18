package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Employee;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataProviderTest extends BaseProviderTest implements IDataProviderTest {
    private static final Logger logger = LogManager.getLogger(CsvDataProviderTest.class);
    private final CsvDataProvider csvProvider = new CsvDataProvider();

    @BeforeAll
    static void deleteFiles() {
        String actualDatasourcePath = Constants.DATASOURCE_TEST_PATH_CSV;

        String projectsFilePath = actualDatasourcePath
                .concat(Constants.PROJECTS_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        String employeesFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEES_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        String tasksFilePath = actualDatasourcePath
                .concat(Constants.TASKS_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        String bugReportsFilePath = actualDatasourcePath
                .concat(Constants.BUG_REPORTS_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        String eventsFilePath = actualDatasourcePath
                .concat(Constants.EVENTS_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        String documentationsFilePath = actualDatasourcePath
                .concat(Constants.DOCUMENTATIONS_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        String employeeProjectFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH).concat(
                Constants.FILE_CSV_EXTENSION);

        ArrayList<String> list = new ArrayList<>() {{
            add(projectsFilePath);
            add(employeesFilePath);
            add(employeeProjectFilePath);
            add(tasksFilePath);
            add(bugReportsFilePath);
            add(eventsFilePath);
            add(documentationsFilePath);
        }};

        list.forEach(path -> {
            File file = new File(path);
            if (file.delete())
                logger.debug("deleteFiles[1]: file {} was deleted", file.getName());
        });
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
    public void createExistingProject() {

    }

    @Override
    @Test
    public void processNewTask() {
        Result<NoData> actual = csvProvider.processNewTask(task);

        logger.debug("processNewTask[1]: actual result code {}", actual.getCode());
        logger.debug("processNewTask[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewTask[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void createExistingTasks() {

    }

    @Override
    @Test
    public void processNewBugReport() {
        Result<NoData> actual = csvProvider.processNewBugReport(bugReport);

        logger.debug("processNewBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("processNewBugReport[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewBugReport[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void createExistingBugReports() {

    }

    @Override
    @Test
    public void processNewDocumentation() {
        Result<NoData> actual = csvProvider.processNewDocumentation(documentation);

        logger.debug("processNewDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void createExistingDocumentation() {

    }

    @Override
    @Test
    public void processNewEvent() {
        Result<NoData> actual = csvProvider.processNewEvent(event);

        logger.debug("processNewEvent[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEvent[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewEvent[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void createExistingEvent() {

    }

    @Override
    @Test
    public void processNewEmployee() {
        csvProvider.deleteEmployee(employee1.getId());
        Result<NoData> actual = csvProvider.processNewEmployee(employee1);

        logger.debug("processNewEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewEmployee[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }


    @Override
    @Test
    public void createExistingEmployee() {

    }

    @Override
    @Test
    public void monitorProjectCharacteristics() {

    }

    @Override
    @Test
    public void monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency() {

    }

    @Override
    @Test
    public void monitorProjectCharacteristicsWithLaborEfficiency() {

    }

    @Override
    @Test
    public void calculateProjectReadiness() {

    }

    @Override
    @Test
    public void calculateProjectReadinessIfHasNoTasks() {

    }

    @Override
    @Test
    public void calculateLaborEfficiency() {

    }

    @Override
    @Test
    public void calculateLaborEfficiencyIfEmployeeHasNoTasks() {

    }

    @Override
    @Test
    public void trackTaskStatus() {

    }

    @Override
    @Test
    public void trackBugReportStatus() {

    }

    @Override
    @Test
    public void bindEmployeeToProject() {

    }

    @Override
    @Test
    public void bindProjectManager() {

    }

    @Override
    @Test
    public void bindTaskExecutor() {

    }

    @Override
    @Test
    public void deleteProject() {
        csvProvider.processNewProject(project1);
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
        csvProvider.processNewBugReport(bugReport);
        Result<NoData> actual = csvProvider.deleteBugReport(bugReport.getId());

        logger.debug("deleteBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("deleteBugReport[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteBugReport[3]: result {}", actual);

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
        csvProvider.processNewEvent(event);
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
    }

    @Override
    @Test
    public void getNonExistentProject() {

    }

    @Override
    @Test
    public void getTasksByTags() {

    }

    @Override
    @Test
    public void getTasksByNonExistentTags() {

    }

    @Override
    @Test
    public void getTasksByProjectId() {

    }

    @Override
    @Test
    public void getTasksFromProjectWithNoTasks() {

    }

    @Override
    @Test
    public void getTasksByNonExistentProjectId() {

    }

    @Override
    @Test
    public void getTasksByEmployeeId() {

    }

    @Override
    @Test
    public void getTasksByNonExistentEmployeeId() {

    }

    @Override
    @Test
    public void getTaskById() {

    }

    @Override
    @Test
    public void getNonExistentTask() {

    }

    @Override
    @Test
    public void getBugReportsByProjectId() {

    }

    @Override
    @Test
    public void getBugReportsByNonExistentProjectId() {

    }

    @Override
    @Test
    public void getBugReportById() {

    }

    @Override
    @Test
    public void getNonExistentBugReport() {

    }

    @Override
    @Test
    public void getEventsByProjectId() {

    }

    @Override
    @Test
    public void getEventsByNonExistentProjectId() {

    }

    @Override
    @Test
    public void getEventById() {

    }

    @Override
    @Test
    public void getNonExistentEvent() {

    }

    @Override
    @Test
    public void getDocumentationsByProjectId() {

    }

    @Override
    @Test
    public void getDocumentationsByNonExistentProjectId() {

    }

    @Override
    @Test
    public void getDocumentationById() {

    }

    @Override
    @Test
    public void getNonExistentDocumentation() {

    }

    @Override
    @Test
    public void getProjectTeam() {

    }

    @Override
    @Test
    public void getProjectTeamOfNonExistentProject() {

    }

    @Override
    @Test
    public void getEmptyProjectTeam() {

    }

    @Override
    @Test
    public void getEmployeeById() {
        csvProvider.processNewEmployee(employee1);
        logger.debug("employee {}", employee1);

        Result<Employee> actual = csvProvider.getEmployeeById(employee1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(employee1, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentEmployee() {

    }
}