package ru.sfedu.projectmanagement.core.api;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.utils.DataSourceType;
import ru.sfedu.projectmanagement.core.utils.DataSourceFileUtil;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;
import ru.sfedu.projectmanagement.core.utils.xml.Wrapper;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFolderIfNotExists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class XmlDataProvider extends DataProvider {
    private final Logger logger = LogManager.getLogger(XmlDataProvider.class);
    private final DataSourceFileUtil dataSourceFileUtil = new DataSourceFileUtil(DataSourceType.XML);


    public XmlDataProvider() {
        try {
            createFolderIfNotExists(dataSourceFileUtil.actualDatasourcePath);
            dataSourceFileUtil.createDatasourceFiles();
        }
        catch (IOException exception) {
            logger.error("Database initialization error: {}", exception.getMessage());
        }
    }


    /**
     * @param project - instance of Project
     * @return
     */
    @Override
    public Result<?> processNewProject(Project project) {
        try {
            Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.createValidation(project);
            if (validateResult.getCode() == ResultCode.SUCCESS) {
                XmlUtil.createRecord(dataSourceFileUtil.projectsFilePath, project);
                logger.debug("processNewProject[1]: project was written in xml {}", project);
                return new Result<>(ResultCode.SUCCESS);
            }
            return validateResult;
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
            Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.createValidation(task);
            if (validateResult.getCode() == ResultCode.SUCCESS) {
                XmlUtil.createRecord(dataSourceFileUtil.tasksFilePath, task);
                logger.debug("processNewTask[1]: task was written in xml {}", task);
                return new Result<>(ResultCode.SUCCESS);
            }
            else return validateResult;
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
            Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.createValidation(bugReport);
            if (validateResult.getCode() == ResultCode.SUCCESS) {
                XmlUtil.createRecord(dataSourceFileUtil.bugReportsFilePath, bugReport);
                logger.debug("processBugReport[1]: bug report was written in xml {}", bugReport);
                return new Result<>(ResultCode.SUCCESS);
            }

            return validateResult;
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
            Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.createValidation(documentation);
            if (validateResult.getCode() == ResultCode.SUCCESS) {
                XmlUtil.createRecord(dataSourceFileUtil.documentationsFilePath, documentation);
                logger.debug("processNewDocumentation[1]: documentation was written in xml {}", documentation);
                return new Result<>(ResultCode.SUCCESS);
            }
            return validateResult;
        }
        catch (JAXBException exception) {
            logger.error("processNewDocumentation[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<?> processNewEmployee(Employee employee) {
        try {
            Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.createValidation(employee);
            if (validateResult.getCode() == ResultCode.SUCCESS) {
                XmlUtil.createRecord(dataSourceFileUtil.employeesFilePath, employee);
                logger.debug("processNewEmployee[1]: employee was written in xml {}", employee);
                return new Result<>(ResultCode.SUCCESS);
            }
            return validateResult;
        }
        catch (JAXBException exception) {
            logger.error("processNewEmployee[2]: {}", exception.getMessage());
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
            Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.createValidation(event);
            if (validateResult.getCode() == ResultCode.SUCCESS) {
                XmlUtil.createRecord(dataSourceFileUtil.eventsFilePath, event);
                logger.debug("processNewEvent[1]: task was written in xml {}", event);
                return new Result<>(ResultCode.SUCCESS);
            }
            return validateResult;
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
    public Result<Project> getProjectById(UUID projectId) {
        Wrapper<Project> projectWrapper = XmlUtil.read(dataSourceFileUtil.projectsFilePath);
       return projectWrapper.getList()
                .stream()
                .filter(p -> p.getId().equals(projectId))
                .map(p -> {
                    logger.debug("getProjectById[1]: received project {}", p);
                    return new Result<>(p, ResultCode.SUCCESS);
                })
                .findFirst()
                .orElseGet(() -> {
                    logger.debug("getProjectById[2]: project with id {} was not found", projectId);
                    return new Result<>(ResultCode.NOT_FOUND);
                });
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<Task> getTaskById(UUID taskId) {
        Wrapper<Task> taskWrapper = XmlUtil.read(dataSourceFileUtil.tasksFilePath);
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

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        Wrapper<BugReport> bugReportWrapper = XmlUtil.read(dataSourceFileUtil.bugReportsFilePath);
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

    /**
     * @param docId
     * @return
     */
    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        Wrapper<Documentation> documentationWrapper = XmlUtil.read(dataSourceFileUtil.documentationsFilePath);
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

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<Event> getEventById(UUID eventId) {
        Wrapper<Event> eventWrapper = XmlUtil.read(dataSourceFileUtil.eventsFilePath);
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

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        Wrapper<Employee> employeeWrapper = XmlUtil.read(dataSourceFileUtil.employeesFilePath);
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
                    return new Result<>(ResultCode.NOT_FOUND);
                });
    }

    /**
     * @param tags
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByTags(ArrayList<String> tags, UUID projectId) {
        Wrapper<Task> taskWrapper = XmlUtil.read(dataSourceFileUtil.tasksFilePath);
        ArrayList<Task> tasks = taskWrapper.getList()
                .stream()
                .filter(task -> task.getTags().containsAll(tags))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (tasks.isEmpty())
            return new Result<>(tasks, ResultCode.NOT_FOUND);
        return new Result<>(tasks, ResultCode.SUCCESS);
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByProjectId(UUID projectId) {
        Wrapper<Task> taskWrapper = XmlUtil.read(dataSourceFileUtil.tasksFilePath);
        ArrayList<Task> tasks = taskWrapper.getList()
                .stream()
                .filter(task -> task.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (tasks.isEmpty())
            return new Result<>(tasks, ResultCode.NOT_FOUND);
        return new Result<>(tasks, ResultCode.SUCCESS);
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) {
        Wrapper<Task> taskWrapper = XmlUtil.read(dataSourceFileUtil.tasksFilePath);
        ArrayList<Task> tasks = taskWrapper.getList()
                .stream()
                .filter(task -> task.getEmployeeId().equals(employeeId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (tasks.isEmpty())
            return new Result<>(tasks, ResultCode.NOT_FOUND);
        return new Result<>(tasks, ResultCode.SUCCESS);
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(UUID projectId) {
        Wrapper<BugReport> bugReportWrapper = XmlUtil.read(dataSourceFileUtil.bugReportsFilePath);
        ArrayList<BugReport> bugReports = bugReportWrapper.getList()
                .stream()
                .filter(bugReport -> bugReport.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (bugReports.isEmpty())
            return new Result<>(bugReports, ResultCode.NOT_FOUND);
        return new Result<>(bugReports, ResultCode.SUCCESS);
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(UUID projectId) {
        Wrapper<Event> eventWrapper = XmlUtil.read(dataSourceFileUtil.eventsFilePath);
        ArrayList<Event> events = eventWrapper.getList()
                .stream()
                .filter(event -> event.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (events.isEmpty())
            return new Result<>(events, ResultCode.NOT_FOUND);
        return new Result<>(events, ResultCode.SUCCESS);
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        Wrapper<Documentation> documentationWrapper = XmlUtil.read(dataSourceFileUtil.documentationsFilePath);
        ArrayList<Documentation> documentations = documentationWrapper.getList()
                .stream()
                .filter(doc -> doc.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (documentations.isEmpty())
            return new Result<>(documentations, ResultCode.NOT_FOUND);
        return new Result<>(documentations, ResultCode.SUCCESS);
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(UUID projectId) {
        Wrapper<EmployeeProjectObject> employeeWrapper = XmlUtil.read(dataSourceFileUtil.employeeProjectFilePath);
        ArrayList<Employee> employees = employeeWrapper.getList()
                .stream()
                .filter(record -> record.getId().equals(projectId))
                .map(record -> getEmployeeById(record.getEmployeeId()))
                .filter(result -> result.getCode() == ResultCode.SUCCESS)
                .map(Result::getData)
                .collect(Collectors.toCollection(ArrayList::new));

        return employees.isEmpty() ?
                new Result<>(ResultCode.NOT_FOUND, "No employees found for the project") :
                new Result<>(employees, ResultCode.SUCCESS);
    }

    /**
     * @param employeeId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        try {
            if (
                XmlUtil.isRecordExists(dataSourceFileUtil.projectsFilePath, projectId) &&
                XmlUtil.isRecordExists(dataSourceFileUtil.employeesFilePath, employeeId)
            ) {
                EmployeeProjectObject linkObject = new EmployeeProjectObject(employeeId, projectId);
                XmlUtil.createRecord(dataSourceFileUtil.employeeProjectFilePath, linkObject);
                return new Result<>(ResultCode.SUCCESS);
            }
            return new Result<>(ResultCode.NOT_FOUND);
        }
        catch (JAXBException exception) {
            logger.error("bindEmployeeToProject[]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param managerId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindProjectManager(UUID managerId, UUID projectId) {
        Result<Employee> employeeResult = getEmployeeById(managerId);
        Result<?> result = new Result<>(ResultCode.SUCCESS);

        if (employeeResult.getCode() != ResultCode.SUCCESS) {
            return new Result<>(ResultCode.ERROR, employeeResult.getMessage());
        }

        Result<TreeMap<String, String>> validateResult = dataSourceFileUtil.checkIfEmployeeBelongsToProject(employeeResult.getData());
        if (validateResult.getCode() != ResultCode.SUCCESS) {
            return validateResult;
        }

        Wrapper<Project> projectWrapper = XmlUtil.read(dataSourceFileUtil.projectsFilePath);
        projectWrapper.getList()
                .stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst()
                .ifPresent(project -> {
                    project.setManager(employeeResult.getData());
                    try {
                        XmlUtil.createOrUpdateRecord(dataSourceFileUtil.projectsFilePath, project);
                        result.setCode(ResultCode.SUCCESS);
                    } catch (JAXBException e) {
                        result.setCode(ResultCode.ERROR);
                        result.setMessage(e.getMessage());
                    }
                });

        return result;
    }


    /**
     * @param executorId
     * @param executorFullName
     * @param taskId
     * @param projectId
     * @return
     */
    @Override
    public Result<Task> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, UUID projectId) {
        Result<Task> validationResult = dataSourceFileUtil.checkEntitiesBeforeBindTaskExecutor(
                executorId, taskId, projectId
        );

        if (validationResult.getCode() != ResultCode.SUCCESS) {
            return validationResult;
        }

        Result<Employee> employeeResult = getEmployeeById(executorId);
        Result<Task> result = new Result<>(null, ResultCode.SUCCESS);
        if (employeeResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(null, ResultCode.ERROR, employeeResult.getMessage());

        Wrapper<Task> taskWrapper = XmlUtil.read(dataSourceFileUtil.tasksFilePath);
        taskWrapper.getList()
                .stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .ifPresent(task -> {
                    task.setEmployeeId(employeeResult.getData().getId());
                    task.setEmployeeFullName(employeeResult.getData().getFullName());
                    try {
                        XmlUtil.createOrUpdateRecord(dataSourceFileUtil.tasksFilePath, task);
                        result.setCode(ResultCode.SUCCESS);
                        result.setData(task);
                    }
                    catch (JAXBException e) {
                        result.setCode(ResultCode.ERROR);
                        result.setMessage(e.getMessage());
                    }
                });

        return result;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<?> deleteProject(UUID projectId) {
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
