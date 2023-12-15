package ru.sfedu.projectmanagement.core.api;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.projectmanagement.core.model.BugReport;
import ru.sfedu.projectmanagement.core.model.Employee;
import ru.sfedu.projectmanagement.core.model.Project;
import ru.sfedu.projectmanagement.core.model.Task;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.DataSourceFileUtil;
import ru.sfedu.projectmanagement.core.utils.DataSourceType;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XmlDataProviderTest extends BaseProviderTest implements IDataProviderTest  {
    private static final Logger logger = LogManager.getLogger(XmlDataProviderTest.class);
    private static final DataProvider xmlDataProvider = new XmlDataProvider();
    private static final DataSourceFileUtil dataSourceFileUtil = new DataSourceFileUtil(DataSourceType.XML);

    @BeforeEach
    void resetDb() throws JAXBException {
        String[] files = {
          dataSourceFileUtil.projectsFilePath,
          dataSourceFileUtil.employeeProjectFilePath,
          dataSourceFileUtil.employeesFilePath,
          dataSourceFileUtil.tasksFilePath,
          dataSourceFileUtil.bugReportsFilePath,
          dataSourceFileUtil.eventsFilePath,
          dataSourceFileUtil.documentationsFilePath
        };

        for (String datasource : files)
            XmlUtil.truncateFile(datasource);
    }


    @Override
    @Order(1)
    @Test
    public void processNewProject() {
        Result<?> actual = xmlDataProvider.processNewProject(project1);

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
        Result<?> actual = xmlDataProvider.processNewProject(project1);

        logger.debug("createExistingProject[1]: actual result code {}", actual.getCode());
        logger.debug("createExistingProject[2]: expected result code: {}", ResultCode.ERROR);
        logger.debug("createExistingProject[3]: result = {}", actual);
        assertEquals(ResultCode.ERROR, actual.getCode());

    }

    @Override
    @Order(3)
    @Test
    public void processNewEmployee() {
        Result<?> actual = xmlDataProvider.processNewEmployee(employee1);

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
        Result<?> actual = xmlDataProvider.processNewEmployee(employee1);

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
            Result<?> actual = xmlDataProvider.processNewTask(task);
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
            Result<?> actual = xmlDataProvider.processNewTask(task);
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

        Result<?> actual = xmlDataProvider.processNewBugReport(bugReport);

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
        Result<?> actual = xmlDataProvider.processNewBugReport(bugReport);

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
        Result<?> actual = xmlDataProvider.processNewDocumentation(documentation);

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
        Result<?> actual = xmlDataProvider.processNewDocumentation(documentation);

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
        Result<?> actual = xmlDataProvider.processNewEvent(event);

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
        Result<?> actual = xmlDataProvider.processNewEvent(event);

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
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByProjectId(project1.getId());

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
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByProjectId(project1.getId());

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
        Result<ArrayList<Task>> actual = xmlDataProvider.getTasksByProjectId(project1.getId());
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
            Result<?> actual = xmlDataProvider.getTaskById(task.getId());

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
        Result<?> actual = xmlDataProvider.getTaskById(UUID.randomUUID());

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
        xmlDataProvider.processNewProject(project1);
        Result<ArrayList<Employee>> actual = xmlDataProvider.getProjectTeam(project1.getId());

        logger.debug("getProjectTeam[1]: actual result code {}", actual.getCode());
        logger.debug("getProjectTeam[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("getProjectTeam[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
        assertEquals(new ArrayList<>(List.of(employee1)), actual.getData());
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
        xmlDataProvider.processNewEmployee(employee2);
        xmlDataProvider.processNewProject(project1);
        Result<?> actual = xmlDataProvider.bindEmployeeToProject(employee1.getId(), project1.getId());
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

        Result<?> actual = xmlDataProvider.bindProjectManager(employee1.getId(), project1.getId());

        logger.debug("bindProjectManager[1]: actual result code {}", actual.getCode());
        logger.debug("bindProjectManager[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("bindProjectManager[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void bindTaskExecutor() {
        xmlDataProvider.processNewEmployee(employee2);
        xmlDataProvider.processNewProject(project1);
        xmlDataProvider.bindEmployeeToProject(employee2.getId(), project1.getId());
        xmlDataProvider.processNewTask(task);

        Result<?> actual = xmlDataProvider.bindTaskExecutor(
                employee2.getId(),
                employee2.getFullName(),
                task.getId(),
                project1.getId()
        );

        logger.debug("bindTaskExecutor[1]: actual result code {}", actual.getCode());
        logger.debug("bindTaskExecutor[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("bindTaskExecutor[1]: errors {}", actual.getErrors());
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteProject() {
        xmlDataProvider.processNewProject(project1);
        Result<?> actual = xmlDataProvider.deleteProject(project1.getId());

        logger.debug("deleteProject[1]: actual result code {}", actual.getCode());
        logger.debug("deleteProject[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteProject[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentProject() {
        Result<?> actual = xmlDataProvider.deleteProject(project1.getId());

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
        Result<?> actual = xmlDataProvider.deleteTask(task.getId());

        logger.debug("deleteTask[1]: actual result code {}", actual.getCode());
        logger.debug("deleteTask[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteTask[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentTask() {
        Result<?> actual = xmlDataProvider.deleteTask(task.getId());

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
        Result<?> actual = xmlDataProvider.deleteBugReport(bugReport.getId());

        logger.debug("deleteBugReport[1]: actual result code {}", actual.getCode());
        logger.debug("deleteBugReport[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteBugReport[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentBugReport() {
        Result<?> actual = xmlDataProvider.deleteBugReport(bugReport.getId());

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
        Result<?> actual = xmlDataProvider.deleteEvent(event.getId());

        logger.debug("deleteEvent[1]: actual result code {}", actual.getCode());
        logger.debug("deleteEvent[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteEvent[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentEvent() {
        xmlDataProvider.processNewEvent(event);
        Result<?> actual = xmlDataProvider.deleteEvent(event.getId());

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
        Result<?> actual = xmlDataProvider.deleteDocumentation(documentation.getId());

        logger.debug("deleteDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("deleteDocumentation[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteDocumentation[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentDocumentation() {
        Result<?> actual = xmlDataProvider.deleteDocumentation(documentation.getId());

        logger.debug("deleteNonExistentDocumentation[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentDocumentation[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentDocumentation[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }

    @Override
    @Test
    public void deleteEmployee() {
        xmlDataProvider.processNewProject(project1);
        Result<?> actual = xmlDataProvider.deleteEmployee(employee1.getId());

        logger.debug("deleteEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("deleteEmployee[2]: expected result code {}", ResultCode.SUCCESS);
        logger.debug("deleteEmployee[3]: result {}", actual);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Override
    @Test
    public void deleteNonExistentEmployee() {
        Result<?> actual = xmlDataProvider.deleteEmployee(employee1.getId());

        logger.debug("deleteNonExistentEmployee[1]: actual result code {}", actual.getCode());
        logger.debug("deleteNonExistentEmployee[2]: expected result code {}", ResultCode.NOT_FOUND);
        logger.debug("deleteNonExistentEmployee[3]: result {}", actual);
        assertEquals(ResultCode.NOT_FOUND, actual.getCode());
    }
}