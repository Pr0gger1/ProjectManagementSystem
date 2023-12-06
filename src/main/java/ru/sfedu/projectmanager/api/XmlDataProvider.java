package ru.sfedu.projectmanager.api;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.Result;
import ru.sfedu.projectmanager.utils.ResultCode;
import ru.sfedu.projectmanager.utils.XmlUtil;

import static ru.sfedu.projectmanager.utils.FileUtil.createFileIfNotExists;
import static ru.sfedu.projectmanager.utils.FileUtil.createFolderIfNotExists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class XmlDataProvider extends DataProvider {
    private final Logger logger = LogManager.getLogger(XmlDataProvider.class);
    private final String projectsFilePath;
    private final String employeesFilePath;
    private final String tasksFilePath;
    private final String bugReportsFilePath;
    private final String eventsFilePath;
    private final String documentationsFilePath;
    private final String employeeProjectFilePath;


    public XmlDataProvider() {
        projectsFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        employeesFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.EMPLOYEES_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        tasksFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.TASKS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        bugReportsFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        eventsFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.EVENTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        documentationsFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        employeeProjectFilePath = Constants.DATASOURCE_PATH_XML
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        try {
            createFolderIfNotExists(Constants.DATASOURCE_PATH_XML);
            createDatasourceFiles();
        }
        catch (IOException exception) {
            logger.error("Database initialization error: {}", exception.getMessage());
        }
    }

    private void createDatasourceFiles() throws IOException {
        createFileIfNotExists(projectsFilePath);
        createFileIfNotExists(employeesFilePath);
        createFileIfNotExists(employeeProjectFilePath);
        createFileIfNotExists(tasksFilePath);
        createFileIfNotExists(bugReportsFilePath);
        createFileIfNotExists(eventsFilePath);
        createFileIfNotExists(documentationsFilePath);
    }

    /**
     * @param project
     * @return
     */
    @Override
    public Result<?> processNewProject(Project project) {
        try {
            XmlUtil.createOrUpdate(projectsFilePath, project);
            logger.debug("processNewProject[1]: project was written in xml {}", project);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("processNewProject[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }

    }

    /**
     * @param task
     * @return
     */
    @Override
    public Result<?> processNewTask(Task task) {
        try {
            XmlUtil.createOrUpdate(tasksFilePath, task);
            logger.debug("processNewTask[1]: task was written in xml {}", task);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("processNewTask[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param bugReport
     * @return
     */
    @Override
    public Result<?> processNewBugReport(BugReport bugReport) {
        try {
            XmlUtil.createOrUpdate(bugReportsFilePath, bugReport);
            logger.debug("processBugReport[1]: bug report was written in xml {}", bugReport);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("processBugReport[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param documentation
     * @return
     */
    @Override
    public Result<?> processNewDocumentation(Documentation documentation) {
        try {
            XmlUtil.createOrUpdate(documentationsFilePath, documentation);
            logger.debug("processNewDocumentation[1]: documentation was written in xml {}", documentation);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("processNewDocumentation[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param event
     * @return
     */
    @Override
    public Result<?> processNewEvent(Event event) {
        try {
            XmlUtil.createOrUpdate(eventsFilePath, event);
            logger.debug("processNewEvent[1]: task was written in xml {}", event);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("processNewEvent[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<Project> getProjectById(String projectId) {
        try {
            Wrapper<Project> projectWrapper = XmlUtil.read(projectsFilePath);
            Project project = projectWrapper.getList()
                    .stream()
                    .filter(p -> p.getId().equals(projectId))
                    .findFirst().orElse(null);

            if (project == null)
                return new Result<>(ResultCode.NOT_FOUND);

            return new Result<>(project, ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("getProjectById");
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByProjectId(String projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<Task> getTaskById(UUID taskId) {
        return null;
    }

    /**
     *
     * @param employeeId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) { return null; }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<Event> getEventById(UUID eventId) {
        return null;
    }

    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Documentation>> getDocumentationsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(String projectId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        return null;
    }

    /**
     * @param projectId
     * @param checkLaborEfficiency
     * @return
     */
    @Override
    public TrackInfo<String, ?> monitorProjectCharacteristics(
            String projectId, boolean checkLaborEfficiency, boolean trackBugs
    ) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public float calculateProjectReadiness(String projectId) {
        return 0;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public TrackInfo<Employee, Float> calculateLaborEfficiency(String projectId) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<Task, String> trackTaskStatus(String projectId) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<BugReport, String> trackBugReportStatus(String projectId) {
        return null;
    }


    /**
     * @param employeeId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindEmployeeToProject(UUID employeeId, String projectId) {
        return null;
    }

    /**
     * @param managerId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindProjectManager(UUID managerId, String projectId) {
        return null;
    }

    /**
     * @param executorId
     * @param taskId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, String projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<?> deleteProject(String projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<?> deleteTask(UUID taskId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<?> deleteBugReport(UUID bugReportId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<?> deleteEvent(UUID eventId) {
        return null;
    }

    /**
     * @param docId
     * @return
     */
    @Override
    public Result<?> deleteDocumentation(UUID docId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<?> deleteEmployee(UUID employeeId) {
        return null;
    }
}
