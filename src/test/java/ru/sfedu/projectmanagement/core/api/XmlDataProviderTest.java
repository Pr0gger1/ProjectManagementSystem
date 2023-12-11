package ru.sfedu.projectmanagement.core.api;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Project;
import ru.sfedu.projectmanagement.core.model.Task;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XmlDataProviderTest extends BaseProviderTest implements IDataProviderTest  {
    private static final Logger logger = LogManager.getLogger(XmlDataProviderTest.class);
    private static final DataProvider xmlDataProvider = new XmlDataProvider();

    @BeforeEach
    void resetDb() throws JAXBException {
        String actualDatasourcePath = Constants.DATASOURCE_TEST_PATH_XML;

        String projectsFilePath = actualDatasourcePath
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String employeesFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEES_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String tasksFilePath = actualDatasourcePath
                .concat(Constants.TASKS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String bugReportsFilePath = actualDatasourcePath
                .concat(Constants.BUG_REPORTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String eventsFilePath = actualDatasourcePath
                .concat(Constants.EVENTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String documentationsFilePath = actualDatasourcePath
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String employeeProjectFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        String[] files = {
          projectsFilePath,
          employeeProjectFilePath,
          employeesFilePath,
          tasksFilePath,
          bugReportsFilePath,
          eventsFilePath,
          documentationsFilePath
        };

        for (String datasource : files)
            XmlUtil.truncateFile(datasource);
    }


    @Override
    @Order(1)
    @Test
    public void processNewProject() {
        Result<?> actual = xmlDataProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewProject[1]: actual result code {}", actual.getCode());
        logger.debug("processNewProject[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[3]: result = {}", actual);
    }

    @Override
    @Order(2)
    @Test
    public void createExistingProject() {
        xmlDataProvider.processNewProject(project);
        Result<?> actual = xmlDataProvider.processNewProject(project);
        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("createExistingProject[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingProject[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingProject[3]: result = {}", actual);
    }

    @Override
    @Order(3)
    @Test
    public void processNewEmployee() {
        Result<?> actual = xmlDataProvider.processNewEmployee(employee);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEmployee[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewEmployee[3]: result = {}", actual);
    }

    @Override
    @Order(4)
    @Test
    public void createExistingEmployee() {
        xmlDataProvider.processNewEmployee(employee);
        Result<?> actual = xmlDataProvider.processNewEmployee(employee);
        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("createExistingEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingEmployee[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingEmployee[3]: result = {}", actual);
    }

    @Override
    @Order(5)
    @Test
    public void processNewTask() {
        tasks.forEach(task -> {
            Result<?> actual = xmlDataProvider.processNewTask(task);
            assertEquals(ResultCode.SUCCESS, actual.getCode());
            int logIndex = 0;

            logger.debug("processNewTask[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("processNewTask[{}]: expected result code: {}", ++logIndex, ResultCode.SUCCESS);
            logger.debug("processNewTask[{}]: result = {}", ++logIndex, actual);
        });
    }

    @Override
    @Order(6)
    @Test
    public void createExistingTasks() {
        tasks.forEach(xmlDataProvider::processNewTask);
        tasks.forEach(task -> {
            Result<?> actual = xmlDataProvider.processNewTask(task);
            assertEquals(ResultCode.ERROR, actual.getCode());
            int logIndex = 0;

            logger.debug("createExistingTask[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("createExistingTask[{}]: expected result code: {}", ++logIndex, ResultCode.ERROR);
            logger.debug("createExistingTask[{}]: result = {}", ++logIndex, actual);
        });
    }

    @Override
    @Order(7)
    @Test
    public void processNewBugReport() {
        Result<?> actual = xmlDataProvider.processNewBugReport(bugReport);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("processNewBugReport[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewBugReport[3]: result = {}", actual);
    }

    @Override
    @Order(8)
    @Test
    public void createExistingBugReports() {
        xmlDataProvider.processNewBugReport(bugReport);
        Result<?> actual = xmlDataProvider.processNewBugReport(bugReport);
        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("createExistingBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingBugReport[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingBugReport[3]: result = {}", actual);
    }

    @Override
    @Order(9)
    @Test
    public void processNewDocumentation() {
        Result<?> actual = xmlDataProvider.processNewDocumentation(documentation);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

        logger.debug("processNewDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[3]: result = {}", actual);
    }

    @Override
    @Order(10)
    @Test
    public void createExistingDocumentation() {
        xmlDataProvider.processNewDocumentation(documentation);
        Result<?> actual = xmlDataProvider.processNewDocumentation(documentation);
        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("createExistingDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingDocumentation[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingDocumentation[3]: result = {}", actual);
    }

    @Override
    @Order(11)
    @Test
    public void processNewEvent() {
        Result<?> actual = xmlDataProvider.processNewEvent(event);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Order(12)
    @Test
    public void createExistingEvent() {
        xmlDataProvider.processNewEvent(event);
        Result<?> actual = xmlDataProvider.processNewEvent(event);
        assertEquals(ResultCode.ERROR, actual.getCode());

        logger.debug("createExistingEvent[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingEvent[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingEvent[3]: result = {}", actual);
    }

    @Override
    @Test
    public void getProjectById() {
        xmlDataProvider.processNewProject(project);
        Result<Project> actual = xmlDataProvider.getProjectById(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(project, actual.getData());

        logger.debug("getProjectById[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getProjectById[3]: result {}", actual);
    }

    @Override
    @Test
    public void getNonExistentProject() {

    }

    @Override
    @Test
    public void getTasksByProjectId() {
        tasks.forEach(xmlDataProvider::processNewTask);
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByProjectId(project.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());

        logger.debug("getTasksByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByProjectId[3]: result {}", actual);
    }

    @Override
    @Test
    public void getTasksFromProjectWithNoTasks() {

    }

    @Override
    @Test
    public void getTasksByNonExistentProjectId() {
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByProjectId(project.getId());
        logger.debug("getTasksByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getTasksByProjectId[1]: actual array length {}", actual.getData().size());
        logger.debug("getTasksByProjectId[2]: expected array length 0");

        logger.debug("getTasksByProjectId[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(new ArrayList<>(), actual.getData());
        assertEquals(0, actual.getData().size());

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
    public void getTasksByEmployeeId() {
    }


    @Override
    @Test
    public void getTasksByNonExistentEmployeeId() {

    }

    @Override
    @Test
    public void getTasksByTags() {
        tasks.forEach(xmlDataProvider::processNewTask);
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByTags(
                new ArrayList<>(List.of("Tag1")),
                project.getId()
        );

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Override
    @Test
    public void getTasksByNonExistentTags() {

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
    public void getDocumentationById() {
    }

    @Override
    @Test
    public void getNonExistentDocumentation() {

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
    public void getProjectTeam() {
    }

    @Override
    @Test
    public void getProjectTeamOfNonExistentProject() {

    }

    @Override
    public void getEmptyProjectTeam() {

    }

    @Override
    @Test
    public void getEmployeeById() {
    }

    @Override
    @Test
    public void getNonExistentEmployee() {

    }

    @Override
    @Test
    public void monitorProjectCharacteristics() {
    }

    @Override
    public void initDataForMonitorProjectCharacteristics() {

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
    }

    @Override
    @Test
    public void deleteNonExistentProject() {

    }

    @Override
    @Test
    public void deleteTask() {
    }

    @Override
    @Test
    public void deleteNonExistentTask() {

    }

    @Override
    @Test
    public void deleteBugReport() {
    }

    @Override
    @Test
    public void deleteNonExistentBugReport() {

    }

    @Override
    @Test
    public void deleteEvent() {
    }

    @Override
    @Test
    public void deleteNonExistentEvent() {

    }

    @Override
    @Test
    public void deleteDocumentation() {
    }

    @Override
    @Test
    public void deleteNonExistentDocumentation() {

    }

    @Override
    @Test
    public void deleteEmployee() {
    }

    @Override
    @Test
    public void deleteNonExistentEmployee() {

    }
}