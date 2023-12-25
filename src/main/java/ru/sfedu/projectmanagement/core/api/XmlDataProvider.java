package ru.sfedu.projectmanagement.core.api;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.ChangeType;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.xml.Wrapper;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.xml.XmlDataChecker;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFileIfNotExists;
import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFolderIfNotExists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class XmlDataProvider extends DataProvider {
    private final Logger logger = LogManager.getLogger(XmlDataProvider.class);
    private final XmlDataChecker xmlChecker;
    private final String projectsFilePath;
    private final String employeesFilePath;
    private final String tasksFilePath;
    private final String bugReportsFilePath;
    private final String eventsFilePath;
    private final String documentationsFilePath;
    private final String employeeProjectFilePath;


    public XmlDataProvider() {
        this(Environment.valueOf(
                ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT)) == Environment.PRODUCTION ?
                Constants.DATASOURCE_PATH_XML :
                Constants.DATASOURCE_TEST_PATH_XML
        );
    }

    public XmlDataProvider(String datasourcePath) {
        projectsFilePath = datasourcePath
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
        employeesFilePath = datasourcePath
                .concat(Constants.EMPLOYEES_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
        tasksFilePath = datasourcePath
                .concat(Constants.TASKS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
        bugReportsFilePath = datasourcePath
                .concat(Constants.BUG_REPORTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
        eventsFilePath = datasourcePath
                .concat(Constants.EVENTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
        documentationsFilePath = datasourcePath
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
        employeeProjectFilePath = datasourcePath
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        xmlChecker = new XmlDataChecker(
                projectsFilePath,
                employeesFilePath,
                tasksFilePath,
                bugReportsFilePath,
                eventsFilePath,
                documentationsFilePath,
                employeeProjectFilePath
        );

        try {
            createFolderIfNotExists(datasourcePath);
            for (String path : getDataSourceFiles()) {
                createFileIfNotExists(path);
            }
        }
        catch (IOException exception) {
            logger.error("Database initialization error: {}", exception.getMessage());
        }
    }

    private ArrayList<String> getDataSourceFiles() {
        return new ArrayList<>() {{
            add(projectsFilePath);
            add(employeesFilePath);
            add(employeeProjectFilePath);
            add(tasksFilePath);
            add(bugReportsFilePath);
            add(eventsFilePath);
            add(documentationsFilePath);
        }};
    }


    @Override
    public Result<NoData> processNewProject(Project project) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            XmlUtil.createRecord(projectsFilePath, project);
            result = initProjectEntities(project);

            logger.debug("processNewProject[1]: project was written in xml {}", project);
        }
        catch (JAXBException exception) {
            logger.error("processNewProject[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            logEntity(
                project,
               "processNewProject",
               result.getCode(),
               ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewTask(Task task) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> validateResult = xmlChecker.checkBeforeCreate(task);
            if (validateResult.getCode() != ResultCode.SUCCESS)
                return validateResult;
            
            XmlUtil.createRecord(tasksFilePath, task);
            logger.debug("processNewTask[1]: task was written in xml {}", task);
        }
        catch (JAXBException exception) {
            logger.error("processNewTask[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            logEntity(
                task,
                "processNewTask",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewBugReport(BugReport bugReport) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> validateResult = xmlChecker.checkBeforeCreate(bugReport);
            if (validateResult.getCode() != ResultCode.SUCCESS)
                return validateResult;
            
            XmlUtil.createRecord(bugReportsFilePath, bugReport);
            logger.debug("processBugReport[1]: bug report was written in xml {}", bugReport);
        }
        catch (JAXBException exception) {
            logger.error("processBugReport[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            logEntity(
                bugReport,
                "processNewBugReport",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewDocumentation(Documentation documentation) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> validateResult = xmlChecker.checkBeforeCreate(documentation);
            if (validateResult.getCode() != ResultCode.SUCCESS)
                return validateResult;
            
            XmlUtil.createRecord(documentationsFilePath, documentation);
            logger.debug("processNewDocumentation[1]: documentation was written in xml {}", documentation);
        }
        catch (JAXBException exception) {
            logger.error("processNewDocumentation[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            logEntity(
                documentation,
                "processNewDocumentation",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewEmployee(Employee employee) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> validateResult = xmlChecker.checkBeforeCreate(employee);
            if (validateResult.getCode() != ResultCode.SUCCESS)
                return validateResult;
            
            XmlUtil.createRecord(employeesFilePath, employee);
            logger.debug("processNewEmployee[1]: employee was written in xml {}", employee);
        }
        catch (JAXBException exception) {
            logger.error("processNewEmployee[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            logEntity(
                employee,
                "processNewEmployee",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<NoData> processNewEvent(Event event) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> validateResult = xmlChecker.checkBeforeCreate(event);
            if (validateResult.getCode() != ResultCode.SUCCESS)
                return validateResult;
                
            XmlUtil.createRecord(eventsFilePath, event);
            logger.debug("processNewEvent[1]: task was written in xml {}", event);
        }
        catch (JAXBException exception) {
            logger.error("processNewEvent[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        finally {
            logEntity(
                event,
                "processNewEvent",
                result.getCode(),
                ChangeType.CREATE
            );
        }
        return result;
    }

    @Override
    public Result<Project> getProjectById(UUID projectId) {
        Wrapper<Project> projectWrapper = XmlUtil.readFile(projectsFilePath);

        return projectWrapper.getList()
            .stream()
            .filter(p -> p.getId().equals(projectId))
            .map(p -> {
                List<Employee> team = getProjectTeam(projectId).getData();
                List<Task> tasks = getTasksByProjectId(projectId).getData();
                List<BugReport> bugReports = getBugReportsByProjectId(projectId).getData();
                List<Event> events = getEventsByProjectId(projectId).getData();
                List<Documentation> documentations = getDocumentationsByProjectId(projectId).getData();

                p.setTeam(team);
                p.setEvents(events);
                p.setDocumentations(documentations);
                p.setTasks(tasks);
                p.setBugReports(bugReports);
                logger.debug("getProjectById[1]: received project {}", p);
                return new Result<>(p, ResultCode.SUCCESS);
            })
            .findFirst()
            .orElseGet(() -> {
                logger.debug("getProjectById[2]: project with id {} was not found", projectId);
                return new Result<>(ResultCode.NOT_FOUND);
            });
    }

    @Override
    public Result<Task> getTaskById(UUID taskId) {
        Wrapper<Task> taskWrapper = XmlUtil.readFile(tasksFilePath);
        return taskWrapper.getList()
            .stream()
            .filter(t -> t.getId().equals(taskId))
            .map(t -> {
                logger.debug("getTaskById[1]: received task {}", t);
                return new Result<>(t, ResultCode.SUCCESS);
            })
            .findFirst()
            .orElseGet(() -> {
                logger.debug("getTaskById[2]: task with id {} was not found", taskId);
                return new Result<>(ResultCode.NOT_FOUND);
            });
    }

    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        Wrapper<BugReport> bugReportWrapper = XmlUtil.readFile(bugReportsFilePath);
        return bugReportWrapper.getList()
            .stream()
            .filter(bg -> bg.getId().equals(bugReportId))
            .map(bg -> {
                logger.debug("getBugReportById[1]: received bug report {}", bg);
                return new Result<>(bg, ResultCode.SUCCESS);
            })
            .findFirst()
            .orElseGet(() -> {
                logger.debug("getBugReportsById[2]: bug report with id {} was not found", bugReportId);
                return new Result<>(ResultCode.NOT_FOUND);
            });
    }

    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        Wrapper<Documentation> documentationWrapper = XmlUtil.readFile(documentationsFilePath);
        return documentationWrapper.getList()
            .stream()
            .filter(doc -> doc.getId().equals(docId))
            .map(doc -> {
                logger.debug("getDocumentationById[1]: received documentation {}", doc);
                return new Result<>(doc, ResultCode.SUCCESS);
            })
            .findFirst()
            .orElseGet(() -> {
                logger.debug("getDocumentationById[2]: documentation with id {} was not found", docId);
                return new Result<>(ResultCode.NOT_FOUND);
            });
    }

    @Override
    public Result<Event> getEventById(UUID eventId) {
        Wrapper<Event> eventWrapper = XmlUtil.readFile(eventsFilePath);
        return eventWrapper.getList()
            .stream()
            .filter(event -> event.getId().equals(eventId))
            .map(event -> {
                logger.debug("getEventById[1]: received event {}", event);
                return new Result<>(event, ResultCode.SUCCESS);
            })
            .findFirst()
            .orElseGet(() -> {
                logger.debug("getEventById[2]: event with id {} was not found", eventId);
                return new Result<>(ResultCode.NOT_FOUND);
            });
    }

    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        Wrapper<Employee> employeeWrapper = XmlUtil.readFile(employeesFilePath);
        return employeeWrapper.getList()
            .stream()
            .filter(employee -> employee.getId().equals(employeeId))
            .map(employee -> {
                logger.debug("getEmployeeById[1]: received employee {}", employee);
                return new Result<>(employee, ResultCode.SUCCESS);
            })
            .findFirst()
            .orElseGet(() -> {
                logger.debug("getEmployeeById[2]: employee with id {} was not found", employeeId);
                return new Result<>(ResultCode.NOT_FOUND, String.format(Constants.ENTITY_NOT_FOUND_MESSAGE, Employee.class.getSimpleName(), employeeId));
            });
    }

    @Override
    public Result<ArrayList<Task>> getTasksByTags(ArrayList<String> tags, UUID projectId) {
        Wrapper<Task> taskWrapper = XmlUtil.readFile(tasksFilePath);
        ArrayList<Task> tasks = taskWrapper.getList()
                .stream()
                .filter(task -> new HashSet<>(task.getTags()).containsAll(tags))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (tasks.isEmpty())
            return new Result<>(tasks, ResultCode.NOT_FOUND);
        return new Result<>(tasks, ResultCode.SUCCESS);
    }

    @Override
    public Result<List<Task>> getTasksByProjectId(UUID projectId) {
        Wrapper<Task> taskWrapper = XmlUtil.readFile(tasksFilePath);
        ArrayList<Task> tasks = taskWrapper.getList()
                .stream()
                .filter(task -> task.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (tasks.isEmpty())
            return new Result<>(tasks, ResultCode.NOT_FOUND);
        return new Result<>(tasks, ResultCode.SUCCESS);
    }

    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) {
        Wrapper<Task> taskWrapper = XmlUtil.readFile(tasksFilePath);
        ArrayList<Task> tasks = taskWrapper.getList()
                .stream()
                .filter(task -> task.getEmployeeId().equals(employeeId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (tasks.isEmpty())
            return new Result<>(tasks, ResultCode.NOT_FOUND);
        return new Result<>(tasks, ResultCode.SUCCESS);
    }

    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(UUID projectId) {
        Wrapper<BugReport> bugReportWrapper = XmlUtil.readFile(bugReportsFilePath);
        ArrayList<BugReport> bugReports = bugReportWrapper.getList()
                .stream()
                .filter(bugReport -> bugReport.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (bugReports.isEmpty())
            return new Result<>(bugReports, ResultCode.NOT_FOUND);
        return new Result<>(bugReports, ResultCode.SUCCESS);
    }

    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(UUID projectId) {
        Wrapper<Event> eventWrapper = XmlUtil.readFile(eventsFilePath);
        ArrayList<Event> events = eventWrapper.getList()
                .stream()
                .filter(event -> event.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (events.isEmpty())
            return new Result<>(events, ResultCode.NOT_FOUND);
        return new Result<>(events, ResultCode.SUCCESS);
    }

    @Override
    public Result<List<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        Wrapper<Documentation> documentationWrapper = XmlUtil.readFile(documentationsFilePath);
        ArrayList<Documentation> documentations = documentationWrapper.getList()
                .stream()
                .filter(doc -> doc.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (documentations.isEmpty())
            return new Result<>(documentations, ResultCode.NOT_FOUND);
        return new Result<>(documentations, ResultCode.SUCCESS);
    }

    @Override
    public Result<List<Employee>> getProjectTeam(UUID projectId) {
        Wrapper<EmployeeProjectObject> employeeWrapper = XmlUtil.readFile(employeeProjectFilePath);
        ArrayList<Employee> employees = employeeWrapper.getList()
            .stream()
            .filter(record -> record.getId().equals(projectId))
            .map(record -> getEmployeeById(record.getEmployeeId()))
            .filter(result -> result.getCode() == ResultCode.SUCCESS)
            .map(Result::getData)
            .collect(Collectors.toCollection(ArrayList::new));

        if (employees.isEmpty())
            return new Result<>(employees, ResultCode.NOT_FOUND, "employees were not found for the project");
        return new Result<>(employees, ResultCode.SUCCESS);
    }

    @Override
    public Result<NoData> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        try {
            Result<NoData> validationResult = xmlChecker.checkProjectAndEmployeeExistence(employeeId, projectId);
            if (validationResult.getCode() != ResultCode.SUCCESS)
                return validationResult;

            EmployeeProjectObject linkObject = new EmployeeProjectObject(employeeId, projectId);
            XmlUtil.createRecord(employeeProjectFilePath, linkObject);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("bindEmployeeToProject[]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> bindProjectManager(UUID managerId, UUID projectId) {
        Result<Employee> employeeResult = getEmployeeById(managerId);
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);

        if (employeeResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(ResultCode.ERROR, employeeResult.getMessage());

        Result<NoData> validateResult = xmlChecker.checkIfEmployeeBelongsToProject(employeeResult.getData());
        if (validateResult.getCode() != ResultCode.SUCCESS)
            return validateResult;

        Wrapper<Project> projectWrapper = XmlUtil.readFile(projectsFilePath);
        projectWrapper.getList()
                .stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst()
                .ifPresent(project -> {
                    project.setManager(employeeResult.getData());
                    try {
                        XmlUtil.createOrUpdateRecord(projectsFilePath, project);
                        result.setCode(ResultCode.SUCCESS);
                    } catch (JAXBException e) {
                        result.setCode(ResultCode.ERROR);
                        result.setMessage(e.getMessage());
                    }
                });

        return result;
    }

    @Override
    public Result<NoData> deleteProject(UUID projectId) {
        Wrapper<Project> projectWrapper = XmlUtil.readFile(projectsFilePath);
        if (!XmlUtil.isRecordExists(projectsFilePath, projectId))
            return new Result<>(ResultCode.NOT_FOUND);

        projectWrapper.setList(projectWrapper.getList()
            .stream()
            .filter(project -> !project.getId().equals(projectId))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );

        try {
            XmlUtil.setContainer(projectsFilePath, projectWrapper);
            logger.info("deleteProject[1]: project with id {} was deleted successfully", projectId);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("deleteProject[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> deleteTask(UUID taskId) {
        if (!XmlUtil.isRecordExists(tasksFilePath, taskId))
            return new Result<>(ResultCode.NOT_FOUND, String.format("Task with id %s doesn't exist", taskId
        ));

        Wrapper<Task> taskWrapper = XmlUtil.readFile(tasksFilePath);
        taskWrapper.setList(
                taskWrapper.getList()
                    .stream()
                    .filter(task -> !task.getId().equals(taskId))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );

        try {
            XmlUtil.setContainer(tasksFilePath, taskWrapper);
            logger.info("deleteTask[1]: task with id {} was deleted successfully", taskId);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("deleteTask[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> deleteBugReport(UUID bugReportId) {
        if (!XmlUtil.isRecordExists(bugReportsFilePath, bugReportId))
            return new Result<>(ResultCode.NOT_FOUND, String.format("bug report with id %s doesn't exist", bugReportId));

        Wrapper<BugReport> bugReportWrapper = XmlUtil.readFile(bugReportsFilePath);
        bugReportWrapper.setList(
                bugReportWrapper.getList()
                        .stream()
                        .filter(bugReport -> !bugReport.getId().equals(bugReportId))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );

        try {
            XmlUtil.setContainer(bugReportsFilePath, bugReportWrapper);
            logger.info("deleteBugReport[1]: bug report with id {} was deleted successfully", bugReportId);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("deleteBugReport[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> deleteEvent(UUID eventId) {
        if (!XmlUtil.isRecordExists(eventsFilePath, eventId))
            return new Result<>(ResultCode.NOT_FOUND);

        Wrapper<Event> eventWrapper = XmlUtil.readFile(eventsFilePath);
        eventWrapper.setList(
                eventWrapper.getList()
                        .stream()
                        .filter(task -> !task.getId().equals(eventId))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );

        try {
            XmlUtil.setContainer(eventsFilePath, eventWrapper);
            logger.info("deleteEvent[1]: event with id {} was deleted successfully", eventId);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("deleteEvent[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> deleteDocumentation(UUID docId) {
        if (!XmlUtil.isRecordExists(documentationsFilePath, docId))
            return new Result<>(ResultCode.NOT_FOUND, String.format("documentation with id %s doesn't exist", docId));

        Wrapper<Documentation> documentationWrapper = XmlUtil.readFile(documentationsFilePath);
        documentationWrapper.setList(
                documentationWrapper.getList()
                        .stream()
                        .filter(task -> !task.getId().equals(docId))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );

        try {
            XmlUtil.setContainer(documentationsFilePath, documentationWrapper);
            logger.info("deleteDocumentation[1]: documentation with id {} was deleted successfully", docId);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("deleteDocumentation[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> deleteEmployee(UUID employeeId) {
        if (!XmlUtil.isRecordExists(employeesFilePath, employeeId))
            return new Result<>(ResultCode.NOT_FOUND);

        Wrapper<Employee> taskWrapper = XmlUtil.readFile(employeesFilePath);
        taskWrapper.setList(
                taskWrapper.getList()
                        .stream()
                        .filter(task -> !task.getId().equals(employeeId))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );

        try {
            XmlUtil.setContainer(employeesFilePath, taskWrapper);
            logger.info("deleteEmployee[1]: employee with id {} was deleted successfully", employeeId);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (JAXBException exception) {
            logger.error("deleteEmployee[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }
}
