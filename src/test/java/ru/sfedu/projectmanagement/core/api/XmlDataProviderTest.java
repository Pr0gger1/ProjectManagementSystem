package ru.sfedu.projectmanagement.core.api;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XmlDataProviderTest extends BaseProviderTest implements IDataProviderTest  {
    private static final Logger logger = LogManager.getLogger(XmlDataProviderTest.class);
    private static final DataProvider xmlDataProvider = new XmlDataProvider();

    @BeforeEach
    void resetDb() throws JAXBException {
        String actualDatasourcePath = Environment.valueOf(ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT)) ==
                Environment.PRODUCTION ? Constants.DATASOURCE_PATH_XML : Constants.DATASOURCE_TEST_PATH_XML;

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
        Result<NoData> actual = xmlDataProvider.processNewProject(project1);

        logger.debug("processNewProject[1]: actual result code {}", actual.getCode());
        logger.debug("processNewProject[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewProject[3]: result = {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

    }

    @Override
    @Order(2)
    @Test
    public void createExistingProject() {
        xmlDataProvider.processNewProject(project1);
        Result<NoData> actual = xmlDataProvider.processNewProject(project1);

        logger.debug("createExistingProject[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingProject[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingProject[3]: result = {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Order(3)
    @Test
    public void processNewEmployee() {
        Result<NoData> actual = xmlDataProvider.processNewEmployee(employee1);

        logger.debug("processNewEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEmployee[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewEmployee[3]: result = {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());

    }

    @Override
    @Order(4)
    @Test
    public void createExistingEmployee() {
        xmlDataProvider.processNewEmployee(employee1);
        Result<NoData> actual = xmlDataProvider.processNewEmployee(employee1);

        logger.debug("createExistingEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingEmployee[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingEmployee[3]: result = {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());

    }

    @Override
    @Order(5)
    @Test
    public void processNewTask() {
        xmlDataProvider.processNewProject(project1);
        tasks.forEach(task -> {
            Result<NoData> actual = xmlDataProvider.processNewTask(task);
            int logIndex = 0;

            logger.debug("processNewTask[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("processNewTask[{}]: expected result code: {}", ++logIndex, ResultCode.SUCCESS);
            logger.debug("processNewTask[{}]: result = {}", ++logIndex, actual);
            assertEquals(ResultCode.SUCCESS, actual.getCode());

        });
    }

    @Override
    @Order(6)
    @Test
    public void createExistingTasks() {
        xmlDataProvider.processNewProject(project1);

        tasks.forEach(xmlDataProvider::processNewTask);
        tasks.forEach(task -> {
            Result<NoData> actual = xmlDataProvider.processNewTask(task);
            int logIndex = 0;

            logger.debug("createExistingTask[{}]: actual result code {}", ++logIndex, actual.getCode());
            logger.debug("createExistingTask[{}]: expected result code: {}", ++logIndex, ResultCode.ERROR);
            logger.debug("createExistingTask[{}]: result = {}", ++logIndex, actual);
            assertEquals(ResultCode.ERROR, actual.getCode());

        });
    }

    @Override
    @Order(7)
    @Test
    public void processNewBugReport() {
        xmlDataProvider.processNewProject(project1);

        Result<NoData> actual = xmlDataProvider.processNewBugReport(bugReport);

        logger.debug("processNewBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("processNewBugReport[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewBugReport[3]: result = {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Order(8)
    @Test
    public void createExistingBugReports() {
        xmlDataProvider.processNewProject(project1);

        xmlDataProvider.processNewBugReport(bugReport);
        Result<NoData> actual = xmlDataProvider.processNewBugReport(bugReport);

        logger.debug("createExistingBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingBugReport[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingBugReport[3]: result = {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Order(9)
    @Test
    public void processNewDocumentation() {
        xmlDataProvider.processNewProject(project1);
        Result<NoData> actual = xmlDataProvider.processNewDocumentation(documentation);

        logger.debug("processNewDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("processNewDocumentation[2]: expected result code: {}", ResultCode.SUCCESS);
        logger.debug("processNewDocumentation[3]: result = {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Order(10)
    @Test
    public void createExistingDocumentation() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewDocumentation(documentation);
        Result<NoData> actual = xmlDataProvider.processNewDocumentation(documentation);

        logger.debug("createExistingDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingDocumentation[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingDocumentation[3]: result = {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Order(11)
    @Test
    public void processNewEvent() {
        xmlDataProvider.processNewProject(project1);
        Result<NoData> actual = xmlDataProvider.processNewEvent(event);

        logger.debug("processNewEvent[1]: actual result code {}", actual.getCode());
        logger.debug("processNewEvent[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("processNewEvent[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Order(12)
    @Test
    public void createExistingEvent() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewEvent(event);
        Result<NoData> actual = xmlDataProvider.processNewEvent(event);

        logger.debug("createExistingEvent[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingEvent[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingEvent[3]: result = {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());
    }

    @Override
    @Order(13)
    @Test
    public void getProjectById() {
        xmlDataProvider.processNewProject(project1);
        Result<Project> actual = xmlDataProvider.getProjectById(project1.getId());

        logger.debug("getProjectById[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getProjectById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(project1, actual.getData());
    }

    @Override
    @Order(14)
    @Test
    public void getNonExistentProject() {
        Result<Project> actual = xmlDataProvider.getProjectById(UUID.randomUUID());

        logger.debug("getNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Order(15)
    @Test
    public void getTasksByProjectId() {
        xmlDataProvider.processNewProject(project1);

        tasks.forEach(xmlDataProvider::processNewTask);
        Result<List<Task>> actual = xmlDataProvider.getTasksByProjectId(project1.getId());

        logger.debug("getTasksByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByProjectId[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Override
    @Order(16)
    @Test
    public void getTasksFromProjectWithNoTasks() {
        xmlDataProvider.processNewProject(project1);
        Result<List<Task>> actual = xmlDataProvider.getTasksByProjectId(project1.getId());

        logger.debug("getTasksFromProjectWithNoTasks[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksFromProjectWithNoTasks[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getTasksFromProjectWithNoTasks[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(new ArrayList<>(), actual.getData());
    }

    @Override
    @Order(17)
    @Test
    public void getTasksByNonExistentProjectId() {
        Result<List<Task>> actual = xmlDataProvider.getTasksByProjectId(project1.getId());
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
    @Order(18)
    @Test
    public void getTaskById() {
        xmlDataProvider.processNewProject(project1);

        tasks.forEach(xmlDataProvider::processNewTask);
        tasks.forEach(task -> {
            Result<Task> actual = xmlDataProvider.getTaskById(task.getId());

            logger.debug("getTaskById[1]: actual result code {}", actual.getCode());
            logger.debug("getTaskById[2]: expected result code {}", ResultCode.SUCCESS);
            logger.debug("getTaskById[3]: result {}", actual);
            assertEquals(ResultCode.SUCCESS, actual.getCode());
            assertEquals(task, actual.getData());
        });
    }

    @Override
    @Order(19)
    @Test
    public void getNonExistentTask() {
        Result<Task> actual = xmlDataProvider.getTaskById(UUID.randomUUID());

        logger.debug("getNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentTask[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getTasksByEmployeeId() {
        xmlDataProvider.processNewProject(project1);
        tasks.forEach(xmlDataProvider::processNewTask);

        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByEmployeeId(employee1.getId());

        logger.debug("getTasksByEmployeeId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByEmployeeId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByEmployeeId[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }


    @Override
    @Test
    public void getTasksByNonExistentEmployeeId() {
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByEmployeeId(UUID.randomUUID());
        logger.debug("getTasksByEmployeeId[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByEmployeeId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getTasksByEmployeeId[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getTasksByTags() {
        xmlDataProvider.processNewProject(project1);
        tasks.forEach(xmlDataProvider::processNewTask);
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByTags(
                new ArrayList<>(List.of("Tag1")),
                project1.getId()
        );

        logger.debug("getTasksByTags[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByTags[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getTasksByTags[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(tasks, actual.getData());
    }

    @Override
    @Test
    public void getTasksByNonExistentTags() {
        tasks.forEach(xmlDataProvider::processNewTask);
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByTags(
                new ArrayList<>(List.of("Tag5")),
                project1.getId()
        );

        logger.debug("getTasksByTags[1]: actual result code {}", actual.getCode());
        logger.debug("getTasksByTags[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getTasksByTags[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getBugReportsByProjectId() {
        xmlDataProvider.processNewProject(project1);

        bugReports.forEach(xmlDataProvider::processNewBugReport);
        Result<ArrayList<BugReport>> actual = xmlDataProvider.getBugReportsByProjectId(project1.getId());

        logger.debug("getBugReportsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getBugReportsByProjectId[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReports, actual.getData());
    }

    @Override
    @Test
    public void getBugReportsByNonExistentProjectId() {
        Result<ArrayList<BugReport>> actual = xmlDataProvider.getBugReportsByProjectId(project1.getId());

        logger.debug("getBugReportsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getBugReportsByNonExistentProjectId[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getBugReportById() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewBugReport(bugReport);

        Result<BugReport> actual = xmlDataProvider.getBugReportById(bugReport.getId());

        logger.debug("getBugReportById[1]: actual result code {}", actual.getCode());
        logger.debug("getBugReportById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getBugReportById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(bugReport, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentBugReport() {
        Result<BugReport> actual = xmlDataProvider.getBugReportById(bugReport.getId());

        logger.debug("getNonExistentBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentBugReport[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentBugReport[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getEventsByProjectId() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewEvent(event);

        Result<ArrayList<Event>> actual = xmlDataProvider.getEventsByProjectId(project1.getId());

        logger.debug("getEventsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getEventsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEventsByProjectId[3]: result {}", actual);

        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(List.of(event)), actual.getData());
    }

    @Override
    @Test
    public void getEventsByNonExistentProjectId() {
        Result<ArrayList<Event>> actual = xmlDataProvider.getEventsByProjectId(project1.getId());

        logger.debug("getEventsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getEventsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getEventsByNonExistentProjectId[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getEventById() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewEvent(event);

        Result<Event> actual = xmlDataProvider.getEventById(event.getId());

        logger.debug("getEventById[1]: actual result code {}", actual.getCode());
        logger.debug("getEventById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEventById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(event, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentEvent() {
        Result<Event> actual = xmlDataProvider.getEventById(event.getId());

        logger.debug("getNonExistentEvent[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEvent[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentEvent[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getDocumentationById() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewDocumentation(documentation);

        Result<Documentation> actual = xmlDataProvider.getDocumentationById(documentation.getId());

        logger.debug("getDocumentationById[1]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getDocumentationById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(documentation, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentDocumentation() {
        Result<Documentation> actual = xmlDataProvider.getDocumentationById(documentation.getId());

        logger.debug("getNonExistentDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentDocumentation[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentDocumentation[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void getDocumentationsByProjectId() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewDocumentation(documentation);

        Result<List<Documentation>> actual = xmlDataProvider.getDocumentationsByProjectId(project1.getId());

        logger.debug("getDocumentationsByProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationsByProjectId[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getDocumentationsByProjectId[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(List.of(documentation)), actual.getData());
    }

    @Override
    @Test
    public void getDocumentationsByNonExistentProjectId() {
        Result<List<Documentation>> actual = xmlDataProvider.getDocumentationsByProjectId(project1.getId());

        logger.debug("getDocumentationsByNonExistentProjectId[1]: actual result code {}", actual.getCode());
        logger.debug("getDocumentationsByNonExistentProjectId[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getDocumentationsByNonExistentProjectId[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getProjectTeam() {
        xmlDataProvider.processNewProject(project1);
        Result<List<Employee>> actual = xmlDataProvider.getProjectTeam(project1.getId());

        logger.debug("getProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeam[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getProjectTeam[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(List.of(employee1)), actual.getData());
    }

    @Override
    @Test
    public void getProjectTeamOfNonExistentProject() {
        Result<List<Employee>> actual = xmlDataProvider.getProjectTeam(project1.getId());

        logger.debug("getProjectTeamOfNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeamOfNonExistentProject[1]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getProjectTeamOfNonExistentProject[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getEmptyProjectTeam() {
        Project project11 = createProject(
                UUID.randomUUID(),
                "mobile bank app",
                "mobile app for bank based on kotlin and swift",
                WorkStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.MARCH, 1, 0,0),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );

        xmlDataProvider.processNewProject(project11);
        Result<List<Employee>> actual = xmlDataProvider.getProjectTeam(project11.getId());

        logger.debug("getEmptyProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getEmptyProjectTeam[2]: expected result {}", ResultCode.NOT_FOUND);
        logger.debug("getEmptyProjectTeam[3]: result {}", actual);

        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertEquals(0, actual.getData().size());
    }

    @Override
    @Test
    public void getEmployeeById() {
        xmlDataProvider.processNewProject(project1);
        Result<Employee> actual = xmlDataProvider.getEmployeeById(employee1.getId());

        logger.debug("getEmployeeById[1]: actual result code {}", actual.getCode());
        logger.debug("getEmployeeById[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getEmployeeById[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(employee1, actual.getData());
    }

    @Override
    @Test
    public void getNonExistentEmployee() {
        Result<Employee> actual = xmlDataProvider.getEmployeeById(employee1.getId());

        logger.debug("getNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("getNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("getNonExistentEmployee[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
        assertNull(actual.getData());
    }

    @Override
    @Test
    public void monitorProjectCharacteristics() {
        initDataForMonitorProjectCharacteristics(xmlDataProvider);

        float projectReadiness = xmlDataProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = xmlDataProvider.trackTaskStatus(project1.getId());

        ProjectStatistics expectedData = new ProjectStatistics();
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);

        ProjectStatistics actual = xmlDataProvider.monitorProjectCharacteristics(project1.getId(), false, false);
        assertEquals(expectedData, actual);

        logger.debug("monitorProjectCharacteristics[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristics[2]: actual {}", expectedData);
    }


    @Override
    @Test
    public void monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency() {
        initDataForMonitorProjectCharacteristics(xmlDataProvider);
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = xmlDataProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = xmlDataProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = xmlDataProvider.calculateLaborEfficiency(project1.getId());

        TrackInfo<BugReport, String> bugStatuses = xmlDataProvider.trackBugReportStatus(project1.getId());
        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);
        expectedData.setBugReportStatus(bugStatuses);

        ProjectStatistics result = xmlDataProvider.monitorProjectCharacteristics(project1.getId(), true, true);
        assertEquals(expectedData, result);

        logger.debug("monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency[1]: actual {}", result);
        logger.debug("monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency[2]: expected {}", expectedData);
    }

    @Override
    @Test
    public void monitorProjectCharacteristicsWithLaborEfficiency() {
        initDataForMonitorProjectCharacteristics(xmlDataProvider);
        ProjectStatistics expectedData = new ProjectStatistics();
        float projectReadiness = xmlDataProvider.calculateProjectReadiness(project1.getId());
        TrackInfo<Task, String> trackTasks = xmlDataProvider.trackTaskStatus(project1.getId());

        TrackInfo<Employee, Float> laborEfficiency = xmlDataProvider.calculateLaborEfficiency(project1.getId());

        expectedData.setProjectReadiness(projectReadiness);
        expectedData.setTaskStatus(trackTasks);
        expectedData.setLaborEfficiency(laborEfficiency);

        ProjectStatistics actual = xmlDataProvider.monitorProjectCharacteristics(project1.getId(), true, false);
        assertEquals(expectedData, actual);

        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[1]: actual {}", actual);
        logger.debug("monitorProjectCharacteristicsWithLaborEfficiency[2]: expected {}", expectedData);
    }

    @Override
    @Test
    public void calculateProjectReadiness() {
        xmlDataProvider.processNewProject(project1);
        ArrayList<Task> tasks1 = new ArrayList<>() {{ addAll(tasks); }};
        int completedTasksCount = (int) tasks1.stream()
                .filter(task -> task.getStatus() == WorkStatus.COMPLETED).count();
        float expectedReadiness = ((float) completedTasksCount / tasks.size()) * 100.0f;
        tasks1.forEach(task ->  {
            task.setEmployeeId(employee1.getId());
            task.setEmployeeFullName(employee1.getFullName());
            xmlDataProvider.processNewTask(task);
//            xmlDataProvider.bindTaskExecutor(employee1.getId(), employee1.getFullName(), task.getId(), project1.getId());
        });

        float actualReadiness = xmlDataProvider.calculateProjectReadiness(project1.getId());
        assertEquals(expectedReadiness, actualReadiness);

        logger.debug("calculateProjectReadiness[1]: actual {}", actualReadiness);
        logger.debug("calculateProjectReadiness[2]: expected {}", expectedReadiness);
    }

    @Override
    @Test
    public void calculateProjectReadinessIfHasNoTasks() {
        float actualReadiness = xmlDataProvider.calculateProjectReadiness(project1.getId());
        float expectedReadiness = 0f;
        assertEquals(expectedReadiness, actualReadiness);

        logger.debug("calculateProjectReadinessIfHasNoTasks[1]: actual project readiness: {}", actualReadiness);
        logger.debug("calculateProjectReadinessIfHasNoTasks[2]: expected project readiness {}", expectedReadiness);
    }

    @Override
    @Test
    public void calculateLaborEfficiency() {
        xmlDataProvider.processNewProject(project1);
        ArrayList<Task> tasks1 = new ArrayList<>() {{addAll(tasks);}};
        tasks1.forEach(task -> task.setStatus(WorkStatus.COMPLETED));

        tasks1.get(0).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 15, 15, 30));
        tasks1.get(1).setCompletedAt(LocalDateTime.of(2023, Month.NOVEMBER, 17, 12,42));
        tasks1.get(2).setCompletedAt(LocalDateTime.of(2023, Month.DECEMBER, 14, 17,24));

        tasks1.forEach(xmlDataProvider::processNewTask);

        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee1, 102.0f);}}
        );

//        tasks1.forEach(task -> {
//            Result<NoData> bindTaskExecutorResult = xmlDataProvider.bindTaskExecutor(
//                    employee1.getId(), employee1.getFullName(), task.getId(), task.getProjectId()
//            );
//            assertEquals(ResultCode.SUCCESS, bindTaskExecutorResult.getCode());
//        });

        TrackInfo<Employee, Float> actual = xmlDataProvider.calculateLaborEfficiency(project1.getId());
        assertEquals(expectedData, actual);

        logger.debug("calculateLaborEfficiency[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiency[2]: actual {}", expectedData);
    }

    @Override
    @Test
    public void calculateLaborEfficiencyIfEmployeeHasNoTasks() {
        xmlDataProvider.processNewProject(project1);
        TrackInfo<Employee, Float> expectedData = new TrackInfo<>(
                new HashMap<>() {{put(employee1, 0f);}}
        );

        TrackInfo<Employee, Float> actual = xmlDataProvider.calculateLaborEfficiency(project1.getId());
        assertEquals(expectedData, actual);

        logger.debug("calculateLaborEfficiencyIfEmployeeHasNoTasks[1]: actual {}", actual);
        logger.debug("calculateLaborEfficiencyIfEmployeeHasNoTasks[2]: actual {}", expectedData);
    }

    @Override
    @Test
    public void trackTaskStatus() {
        xmlDataProvider.processNewProject(project1);
        tasks.forEach(xmlDataProvider::processNewTask);
        bugReports.forEach(xmlDataProvider::processNewBugReport);

        TrackInfo<Task, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            for (Task task : tasks) put(task, task.getStatus().name());
        }});

        TrackInfo<Task, String> trackInfoActual = xmlDataProvider.trackTaskStatus(project1.getId());
        assertEquals(trackInfoExpected, trackInfoActual);

        logger.debug("trackTaskStatus[1]: actual {}", trackInfoActual);
        logger.debug("trackTaskStatus[2]: expected {}", trackInfoExpected);
    }

    @Override
    @Test
    public void trackBugReportStatus() {
        xmlDataProvider.processNewProject(project1);
        TrackInfo<BugReport, String> trackInfoExpected = new TrackInfo<>(new HashMap<>() {{
            bugReports.forEach(bugReport -> put(bugReport, bugReport.getStatus().name()));
        }});

        bugReports.forEach(xmlDataProvider::processNewBugReport);

        TrackInfo<BugReport, String> trackInfoActual = xmlDataProvider.trackBugReportStatus(project1.getId());
        logger.debug("trackBugReportStatus[1]: expected {}", trackInfoExpected);
        logger.debug("trackBugReportStatus[2]: actual {}", trackInfoActual);
        assertEquals(trackInfoExpected, trackInfoActual);
    }

    @Override
    @Test
    public void bindEmployeeToProject() {
        xmlDataProvider.processNewEmployee(employee2);
        xmlDataProvider.processNewProject(project1);
        Result<NoData> actual = xmlDataProvider.bindEmployeeToProject(employee1.getId(), project1.getId());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void bindProjectManager() {
        Project project1 = createProject(
                UUID.randomUUID(),
                "mobile bank app 1",
                "mobile app for bank based on kotlin and swift",
                WorkStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.MARCH, 1, 0,0),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(List.of(employee1, employee2)),
                employee1
            );

        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewEmployee(employee2);

        Result<NoData> actual = xmlDataProvider.bindProjectManager(employee1.getId(), project1.getId());

        logger.debug("bindProjectManager[1]: actual result code {}", actual.getCode());
        logger.debug("bindProjectManager[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("bindProjectManager[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

//    @Override
//    @Test
//    public void bindTaskExecutor() {
//        xmlDataProvider.processNewEmployee(employee2);
//        xmlDataProvider.processNewProject(project1);
//        xmlDataProvider.bindEmployeeToProject(employee2.getId(), project1.getId());
//        xmlDataProvider.processNewTask(task);
//
//        Result<NoData> actual = xmlDataProvider.bindTaskExecutor(
//                employee2.getId(),
//                employee2.getFullName(),
//                task.getId(),
//                project1.getId()
//        );
//
//        logger.debug("bindTaskExecutor[1]: actual result code {}", actual.getCode());
//        logger.debug("bindTaskExecutor[2]: expected result code {}", ResultCode.SUCCESS);
//        logger.debug("bindTaskExecutor[1]: errors {}", actual.getErrors());
//        assertEquals(ResultCode.SUCCESS, actual.getCode());
//    }

    @Override
    @Test
    public void deleteProject() {
        xmlDataProvider.processNewProject(project1);
        Result<NoData> actual = xmlDataProvider.deleteProject(project1.getId());

        logger.debug("deleteProject[1]: actual result code {}", actual.getCode());
        logger.debug("deleteProject[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteProject[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentProject() {
        Result<NoData> actual = xmlDataProvider.deleteProject(project1.getId());

        logger.debug("deleteNonExistentProject[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentProject[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentProject[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteTask() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewTask(task);
        Result<NoData> actual = xmlDataProvider.deleteTask(task.getId());

        logger.debug("deleteTask[1]: actual result code {}", actual.getCode());
        logger.debug("deleteTask[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteTask[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentTask() {
        Result<NoData> actual = xmlDataProvider.deleteTask(task.getId());

        logger.debug("deleteNonExistentTask[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentTask[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentTask[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteBugReport() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewBugReport(bugReport);
        Result<NoData> actual = xmlDataProvider.deleteBugReport(bugReport.getId());

        logger.debug("deleteBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("deleteBugReport[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteBugReport[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentBugReport() {
        Result<NoData> actual = xmlDataProvider.deleteBugReport(bugReport.getId());

        logger.debug("deleteBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("deleteBugReport[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteBugReport[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteEvent() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewEvent(event);
        Result<NoData> actual = xmlDataProvider.deleteEvent(event.getId());

        logger.debug("deleteEvent[1]: actual result code {}", actual.getCode());
        logger.debug("deleteEvent[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteEvent[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentEvent() {
        xmlDataProvider.processNewEvent(event);
        Result<NoData> actual = xmlDataProvider.deleteEvent(event.getId());

        logger.debug("deleteNonExistentEvent[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentEvent[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentEvent[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteDocumentation() {
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.processNewDocumentation(documentation);
        Result<NoData> actual = xmlDataProvider.deleteDocumentation(documentation.getId());

        logger.debug("deleteDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("deleteDocumentation[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteDocumentation[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentDocumentation() {
        Result<NoData> actual = xmlDataProvider.deleteDocumentation(documentation.getId());

        logger.debug("deleteNonExistentDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentDocumentation[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentDocumentation[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteEmployee() {
        xmlDataProvider.processNewProject(project1);
        Result<NoData> actual = xmlDataProvider.deleteEmployee(employee1.getId());

        logger.debug("deleteEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("deleteEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteEmployee[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentEmployee() {
        Result<NoData> actual = xmlDataProvider.deleteEmployee(employee1.getId());

        logger.debug("deleteNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentEmployee[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }
}