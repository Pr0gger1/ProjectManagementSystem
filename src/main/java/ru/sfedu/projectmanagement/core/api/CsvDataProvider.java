package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.ChangeType;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.csv.CsvDataChecker;
import ru.sfedu.projectmanagement.core.utils.csv.CsvUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFileIfNotExists;
import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFolderIfNotExists;

public class CsvDataProvider extends DataProvider {
    private final Logger logger = LogManager.getLogger(CsvDataProvider.class);
    private final CsvDataChecker csvChecker;
    private final String projectsFilePath;
    private final String employeesFilePath;
    private final String tasksFilePath;
    private final String bugReportsFilePath;
    private final String eventsFilePath;
    private final String documentationsFilePath;
    private final String employeeProjectFilePath;
    private final String taskTagsFilePath;
    private final String documentationDataFilePath;
    private final String managerProjectFilePath;

    public CsvDataProvider() {
        this(Environment.valueOf(
                ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT)) == Environment.PRODUCTION ?
                Constants.DATASOURCE_PATH_CSV :
                Constants.DATASOURCE_TEST_PATH_CSV
        );
    }

    public CsvDataProvider(String datasourcePath) {
        projectsFilePath = datasourcePath
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        employeesFilePath = datasourcePath
                .concat(Constants.EMPLOYEES_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        tasksFilePath = datasourcePath
                .concat(Constants.TASKS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        bugReportsFilePath = datasourcePath
                .concat(Constants.BUG_REPORTS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        eventsFilePath = datasourcePath
                .concat(Constants.EVENTS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        documentationsFilePath = datasourcePath
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        employeeProjectFilePath = datasourcePath
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        taskTagsFilePath = datasourcePath
                .concat(Constants.TASK_TAG_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        documentationDataFilePath = datasourcePath
                .concat(Constants.DOCUMENTATION_DATA_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);
        managerProjectFilePath = datasourcePath
                .concat(Constants.MANAGER_PROJECT_FILE_PATH)
                .concat(Constants.FILE_CSV_EXTENSION);

        csvChecker = new CsvDataChecker(
                projectsFilePath,
                employeesFilePath,
                tasksFilePath,
                bugReportsFilePath,
                eventsFilePath,
                documentationsFilePath,
                employeeProjectFilePath
        );
        ArrayList<String> dataSourceFiles = getDataSourceFiles();

        try {
            createFolderIfNotExists(datasourcePath);
            dataSourceFiles.forEach(file -> {
                try { createFileIfNotExists(file); }
                catch (IOException e) {
                    logger.error("CsvDataProvider[1]: create datasource file error: {}", e.getMessage());
                }
            });

        }
        catch (IOException exception) {
            logger.error("CsvDataProvider[1]: create datasource dir error: {}", exception.getMessage());
        }
    }

    private ArrayList<String> getDataSourceFiles() {
        return new ArrayList<>() {{
            add(projectsFilePath);
            add(employeesFilePath);
            add(tasksFilePath);
            add(bugReportsFilePath);
            add(eventsFilePath);
            add(documentationsFilePath);
            add(employeeProjectFilePath);
            add(taskTagsFilePath);
            add(documentationDataFilePath);
            add(managerProjectFilePath);
        }};
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewProject(Project)}
     */
    @Override
    public Result<NoData> processNewProject(Project project) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            if (project.getManager() != null) {
                ManagerProjectObject managerLink = new ManagerProjectObject(project.getManager().getId(), project.getId());
                CsvUtil.createRecord(managerProjectFilePath, managerLink, EmployeeProjectObject.class);
            }
            CsvUtil.createRecord(projectsFilePath, project, Project.class);
            result = initProjectEntities(project);

            logger.info("processNewProject[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", project.getId()
            ));

            return result;
        }
        catch (Exception exception) {
            logger.error("processNewProject[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
        finally {
            logEntity(
                project,
                "processNewProject",
                result.getCode(),
                ChangeType.CREATE
            );
        }
    }

    /**
     * @param task instance of Task
     * @return result with execution code and message if it fails
     */
    private Result<NoData> processNewTaskTags(Task task) {
        try {
            // collecting tags into list of TaskTag objects
            List<TaskTag> tags = task.getTags()
                    .stream()
                    .map(tag -> new TaskTag(task.getId(), tag))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            if (!tags.isEmpty()) {
                tags.forEach(tag -> {
                    try {
                        CsvUtil.createRecord(taskTagsFilePath, tag, TaskTag.class);
                    } catch (Exception e) {
                        logger.debug("processNewTaskTags[2]: {}", e.getMessage());
                    }
                });
                logger.debug("processNewTaskTags[2]: tags was saved successfully");
            }

            return new Result<>(ResultCode.SUCCESS);
        }
        catch (Exception exception) {
            logger.error("processNewTaskTags[3]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * @param tasks instance of Task that will be assigned a list of tags
     */
    private void setTaskTags(List<Task> tasks) {
        List<TaskTag> tags = Optional.ofNullable(CsvUtil.readFile(taskTagsFilePath, TaskTag.class))
                .orElse(new ArrayList<>());

        Map<UUID, List<String>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(TaskTag::getTaskId,
                        Collectors.mapping(TaskTag::getTag, Collectors.toList())));

        tasks.forEach(task -> task.setTags(
                tagMap.getOrDefault(task.getId(), Collections.emptyList()))
        );
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewTask(Task)}
     */
    @Override
    public Result<NoData> processNewTask(Task task) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> checkConstraintResult = csvChecker.checkBeforeCreate(task);
            if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
                return checkConstraintResult;

            result = processNewTaskTags(task);
            CsvUtil.createRecord(tasksFilePath, task, Task.class);

            logger.info("processNewTask[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", task.getId()
            ));

            return result;
        }
        catch (Exception exception) {
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

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewBugReport(BugReport)}
     */
    @Override
    public Result<NoData> processNewBugReport(BugReport bugReport) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> checkConstraintResult = csvChecker.checkBeforeCreate(bugReport);
            if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
                return checkConstraintResult;

            CsvUtil.createRecord(bugReportsFilePath, bugReport, BugReport.class);

            logger.info("processNewBugReport[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", bugReport.getId()
            ));

            return result;
        }
        catch (Exception exception) {
            logger.error("processNewBugReport[2]: {}", exception.getMessage());
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

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewDocumentation(Documentation)}
     */
    @Override
    public Result<NoData> processNewDocumentation(Documentation documentation) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            Result<NoData> checkConstraintResult = csvChecker.checkBeforeCreate(documentation);
            if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
                return checkConstraintResult;

            HashMap<String, String> body = documentation.getBody();

            List<DocumentationData> data = new ArrayList<>();
            body.forEach((key, value) -> data.add(new DocumentationData(documentation.getId(), key, value)));
            data.forEach(d -> {
                try {
                    CsvUtil.createRecord(documentationDataFilePath, d, DocumentationData.class);
                } catch (Exception e) {
                    logger.error("processNewDocumentation[1]: {}", e.getMessage());
                    result.setCode(ResultCode.ERROR);
                    result.setMessage(e.getMessage());
                }
            });
            CsvUtil.createRecord(documentationsFilePath, documentation, Documentation.class);

            logger.info("processNewDocumentation[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", documentation.getId()
            ));

            return result;
        }
        catch (Exception exception) {
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

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewEvent(Event)}
     */
    @Override
    public Result<NoData> processNewEvent(Event event) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
           Result<NoData> checkConstraintResult = csvChecker.checkBeforeCreate(event);
           if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
               return checkConstraintResult;

            CsvUtil.createRecord(eventsFilePath, event, Event.class);

            logger.info("processNewEvent[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", event.getId()
            ));

            return result;
        }
        catch (Exception exception) {
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

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#processNewEmployee(Employee)}
     */
    @Override
    public Result<NoData> processNewEmployee(Employee employee) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        try {
            CsvUtil.createRecord(employeesFilePath, employee, Employee.class);
            logger.info("processNewEmployee[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", employee.getId()
            ));
            return result;
        }
        catch (Exception exception) {
            logger.error("createRecord[2]: {}", exception.getMessage());
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

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#bindEmployeeToProject(UUID, UUID)}
     */
    @Override
    public Result<NoData> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        try {
            Result<NoData> checkConstraintResult = csvChecker.checkProjectAndEmployeeExistence(employeeId, projectId);
            if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
                return checkConstraintResult;

            EmployeeProjectObject obj = new EmployeeProjectObject(employeeId, projectId);
            CsvUtil.createRecord(employeeProjectFilePath, obj, EmployeeProjectObject.class);
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (Exception exception) {
            logger.error("bindEmployeeToProject[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#bindProjectManager(UUID, UUID)}
     */
    @Override
    public Result<NoData> bindProjectManager(UUID managerId, UUID projectId) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        Result<NoData> checkConstraintResult = csvChecker.checkProjectAndEmployeeExistence(managerId, projectId);

        if (checkConstraintResult.getCode() != ResultCode.SUCCESS)
            return checkConstraintResult;

        Employee manager = Optional.ofNullable(CsvUtil.readFile(employeesFilePath, Employee.class))
                .flatMap(employees -> employees.stream()
                        .filter(employee -> employee.getId().equals(managerId))
                        .findFirst())
                .orElse(null);

        try {
            List<Project> projectList = CsvUtil.readFile(projectsFilePath, Project.class);
            projectList = Optional.ofNullable(projectList)
                    .map(projects -> projects
                            .stream()
                            .peek(project -> {
                                if (project.getId().equals(projectId)) project.setManager(manager);
                            }).toList())
                    .orElse(new ArrayList<>());

            CsvUtil.createRecords(projectsFilePath, projectList, Project.class);
            return result;
        }
        catch (Exception exception) {
            logger.error("bindProjectManager[]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteProject(UUID)}
     */
    @Override
    public Result<NoData> deleteProject(UUID projectId) {
        AtomicReference<Project> projectBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Project> data = CsvUtil.readFile(projectsFilePath, Project.class);
            data = Optional.ofNullable(data)
                .map(projects -> projects.stream()
                        .filter(project -> {
                    if (project.getId().equals(projectId)) {
                        result.setCode(ResultCode.SUCCESS);
                        projectBean.set(project);
                        return false;
                    }
                    return true;
            }).toList()).orElse(new ArrayList<>());

            CsvUtil.createRecords(projectsFilePath, data, Project.class);

            logger.info("deleteProject[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "project", projectId
            ));

            return result;
        }
        catch (Exception exception) {
            logger.debug("deleteProject[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
        finally {
            logEntity(
                projectBean.get(),
                "deleteProject",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteTask(UUID)}
     */
    @Override
    public Result<NoData> deleteTask(UUID taskId) {
        AtomicReference<Task> taskBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Task> taskList = CsvUtil.readFile(tasksFilePath, Task.class);
            List<TaskTag> taskTagList = CsvUtil.readFile(taskTagsFilePath, TaskTag.class);

            taskTagList = Optional.ofNullable(taskTagList)
                    .map(taskTags -> taskTags.stream()
                            .filter(taskTag -> !taskTag.getId().equals(taskId))
                            .toList())
                    .orElse(new ArrayList<>());

            taskList = Optional.ofNullable(taskList)
                    .map(tasks -> tasks.stream()
                            .filter(task -> {
                                if (task.getId().equals(taskId)) {
                                    result.setCode(ResultCode.SUCCESS);
                                    taskBean.set(task);
                                    return false;
                                }
                                return true;
            }).toList())
                    .orElse(new ArrayList<>());

            CsvUtil.createRecords(tasksFilePath, taskList, Task.class);
            CsvUtil.createRecords(taskTagsFilePath, taskTagList, TaskTag.class);
            logger.info("deleteTask[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", taskId
            ));
            return result;
        }
        catch (Exception exception) {
            logger.error("deleteTask[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
        finally {
            logEntity(
                taskBean,
                "deleteTask",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteBugReport(UUID)}
     */
    @Override
    public Result<NoData> deleteBugReport(UUID bugReportId) {
        AtomicReference<BugReport> bugReportBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<BugReport> data = CsvUtil.readFile(bugReportsFilePath, BugReport.class);
            data = Optional.ofNullable(data).map(bugreports -> bugreports.stream().filter(bugReport -> {
                if(bugReport.getId().equals(bugReportId)) {
                    result.setCode(ResultCode.SUCCESS);
                    bugReportBean.set(bugReport);
                    return false;
                }
                return true;
            }).toList()).orElse(new ArrayList<>());

            CsvUtil.createRecords(bugReportsFilePath, data, BugReport.class);

            logger.info("deleteBugReport[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", bugReportId
            ));
            return result;
        }
        catch (Exception exception) {
            logger.debug("deleteBugReport[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
        finally {
            logEntity(
                    bugReportBean,
                    "deleteBugReport",
                    result.getCode(),
                    ChangeType.DELETE
            );
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteEvent(UUID)}
     */
    @Override
    public Result<NoData> deleteEvent(UUID eventId) {
        AtomicReference<Event> eventBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Event> data = CsvUtil.readFile(eventsFilePath, Event.class);
            data = Optional.ofNullable(data)
                .map(events -> events.stream().filter(event -> {
                    if(event.getId().equals(eventId)) {
                        result.setCode(ResultCode.SUCCESS);
                        eventBean.set(event);
                        return false;
                    }
                    return true;
            }).toList()).orElseGet(() -> {
                result.setCode(ResultCode.NOT_FOUND);
                return new ArrayList<>();
            });

            CsvUtil.createRecords(eventsFilePath, data, Event.class);

            logger.info("deleteEvent[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", eventId
            ));
            return result;
        }
        catch (Exception exception) {
            logger.debug("deleteEvent[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
        finally {
            logEntity(
                    eventBean,
                    "deleteEvent",
                    result.getCode(),
                    ChangeType.DELETE
            );
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteDocumentation(UUID)}
     */
    @Override
    public Result<NoData> deleteDocumentation(UUID docId) {
        AtomicReference<Documentation> docBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Documentation> documentationList = CsvUtil.readFile(documentationsFilePath, Documentation.class);
            List<DocumentationData> docDataList = CsvUtil.readFile(documentationDataFilePath, DocumentationData.class);

            docDataList = Optional.ofNullable(docDataList)
                    .map(docData -> docData.stream().filter(doc -> !doc.getId().equals(docId)).toList())
                    .orElse(new ArrayList<>());

            documentationList = Optional.ofNullable(documentationList).map(docs -> docs.stream().filter(doc -> {
                if(doc.getId().equals(docId)) {
                    result.setCode(ResultCode.SUCCESS);
                    docBean.set(doc);
                    return false;
                }
                return true;
            }).toList()).orElse(new ArrayList<>());

            CsvUtil.createRecords(documentationsFilePath, documentationList, Documentation.class);
            CsvUtil.createRecords(documentationDataFilePath, docDataList, DocumentationData.class);

            logger.info("deleteDocumentation[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", docId
            ));
            return result;
        }
        catch (Exception exception) {
            logger.debug("deleteDocumentation[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
        finally {
            logEntity(
                    docBean,
                    "deleteDocumentation",
                    result.getCode(),
                    ChangeType.DELETE
            );
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#deleteEmployee(UUID)}
     */
    @Override
    public Result<NoData> deleteEmployee(UUID employeeId) {
        AtomicReference<Employee> employeeBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Employee> data = CsvUtil.readFile(employeesFilePath, Employee.class);
            data = Optional.ofNullable(data).map(employees -> employees.stream().filter(task -> {
                if(task.getId().equals(employeeId)) {
                    result.setCode(ResultCode.SUCCESS);
                    employeeBean.set(task);
                    return false;
                }
                return true;
            }).toList()).orElse(new ArrayList<>());

            CsvUtil.createRecords(employeesFilePath, data, Employee.class);

            logger.info("deleteEmployee[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", employeeId
            ));
            return result;
        }
        catch (Exception exception) {
            logger.debug("deleteEmployee[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR);
        }
        finally {
            logEntity(
                employeeBean,
                "deleteEmployee",
                result.getCode(),
                ChangeType.DELETE
            );
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getProjectById(UUID)}
     */
    @Override
    public Result<Project> getProjectById(UUID projectId) {
        try {
            List<Project> projectList = CsvUtil.readFile(projectsFilePath, Project.class);

            // getting list of employees who belong to the project
            List<UUID> employeeIds = Optional.ofNullable(CsvUtil.readFile(employeeProjectFilePath, EmployeeProjectObject.class))
                    .map(ep -> ep.stream()
                    .filter(employee -> employee.getProjectId().equals(projectId))
                    .map(EmployeeProjectObject::getEmployeeId).toList())
                    .orElse(new ArrayList<>());

            // getting manager id
            UUID managerId = Optional.ofNullable(CsvUtil.readFile(managerProjectFilePath, ManagerProjectObject.class))
                    .flatMap(links -> links.stream()
                            .filter(link -> link.getProjectId().equals(projectId))
                            .findFirst()
                            .map(EmployeeProjectObject::getEmployeeId))
                    .orElse(null);

            List<Employee> employees = Optional.ofNullable(CsvUtil.readFile(employeesFilePath, Employee.class))
                    .map(e -> e.stream()
                            .filter(employee -> employeeIds.contains(employee.getId())).toList())
                    .orElse(new ArrayList<>());

            Employee manager = employees.stream()
                    .filter(employee -> employee.getId().equals(managerId))
                    .findFirst()
                    .orElse(null);

            List<Task> tasks = getTasksByProjectId(projectId).getData();
            List<BugReport> bugReports = getBugReportsByProjectId(projectId).getData();
            List<Event> events = getEventsByProjectId(projectId).getData();
            List<Documentation> documentations = getDocumentationsByProjectId(projectId).getData();

            return Optional.ofNullable(projectList)
                    .map(projects -> projects.stream()
                            .filter(project -> project.getId().equals(projectId))
                            .findFirst()
                            .map(project -> {
                                project.setTeam(new ArrayList<>(employees));
                                project.setTasks(tasks);
                                project.setEvents(events);
                                project.setBugReports(bugReports);
                                project.setDocumentations(documentations);
                                project.setManager(manager);

                                logger.debug("getProjectById[1]: received project {}", project);
                                return new Result<>(project, ResultCode.SUCCESS);
                            })
                            .orElseGet(() -> {
                                String message = String.format(
                                        Constants.ENTITY_NOT_FOUND_MESSAGE, Project.class.getSimpleName(), projectId
                                );

                                logger.debug("getProjectById[2]: {}", message);
                                return new Result<>(ResultCode.NOT_FOUND, message);
                            }))
                .orElseGet(() -> {
                    logger.debug("getProjectById[3]: {}", Constants.READ_ERROR);
                    return new Result<>(ResultCode.ERROR, Constants.READ_ERROR);
                });
        }
        catch (Exception exception) {
            logger.error("getProjectById[4]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTasksByProjectId(UUID)}
     */
    @Override
    public Result<List<Task>> getTasksByProjectId(UUID projectId) {
        Result<NoData> checkProjectResult = csvChecker.checkProjectExistence(projectId);
        if (checkProjectResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, checkProjectResult.getMessage());

        try {
            List<Task> taskList = Optional.ofNullable(CsvUtil.readFile(tasksFilePath, Task.class))
                    .orElse(new ArrayList<>());

            setTaskTags(taskList);

            return Optional.of(taskList)
                    .map(tasks -> tasks.stream()
                            .filter(task -> task.getProjectId().equals(projectId))
                            .collect(Collectors.toList()))
                    .map(tasks -> {
                        logger.debug("getTasksByProjectId[1]: received tasks {}", tasks);
                        return new Result<>(tasks, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        String message = String.format("project with id %s has no tasks", projectId);
                        logger.debug("getTasksByProjectId[2]: {}", message);
                        return new Result<>(new ArrayList<>(), ResultCode.SUCCESS, message);
                    });
        }
        catch (Exception exception) {
            logger.error("getTasksByProjectId[3]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTasksByEmployeeId(UUID)}
     */
    @Override
    public Result<List<Task>> getTasksByEmployeeId(UUID employeeId) {
        if (CsvUtil.isRecordNotExists(employeesFilePath, employeeId, Employee.class))
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, String.format(
                    Constants.ENTITY_NOT_FOUND_MESSAGE,
                    Employee.class.getSimpleName(), employeeId
            ));

        try {
            List<Task> taskList = Optional.ofNullable(CsvUtil.readFile(tasksFilePath, Task.class))
                    .map(tasks -> tasks.stream().filter(task -> task.getEmployeeId().equals(employeeId)).toList())
                    .orElse(new ArrayList<>());

            setTaskTags(taskList);

            return Optional.of(taskList)
                    .map(tasks -> tasks.stream()
                            .filter(task -> task.getEmployeeId().equals(employeeId))
                            .toList())
                    .map(tasks -> {
                        logger.debug("getTasksByEmployeeId[1]: received tasks {}", tasks);
                        return new Result<>(tasks, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        String message = String.format(
                                Constants.ENTITY_NOT_FOUND_MESSAGE,
                                Employee.class.getSimpleName(),
                                employeeId
                        );

                        logger.debug("getTasksByEmployeeId[2]: {}", message);
                        return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, message);
                    });
        }
        catch (Exception exception) {
            logger.error("getTasksByEmployeeId[3]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTaskById(UUID)}
     */
    @Override
    public Result<Task> getTaskById(UUID taskId) {
        try {
            List<Task> taskList = CsvUtil.readFile(tasksFilePath, Task.class);
            return Optional.ofNullable(taskList)
                .map(tasks -> tasks.stream()
                        .filter(task -> task.getId().equals(taskId))
                                .findFirst()
                                .map(task -> {
                                    logger.debug("getTaskById[1]: received task {}", task);
                                    return new Result<>(task, ResultCode.SUCCESS);
                                })
                                .orElseGet(() -> {
                                    String message = String.format(
                                            Constants.ENTITY_NOT_FOUND_MESSAGE,
                                            Task.class.getSimpleName(), taskId
                                    );
                                    logger.debug("getTaskId[2]: {}", message);
                                    return new Result<>(ResultCode.NOT_FOUND, message);
                                })
                        )
                .orElseGet(() -> {
                    logger.debug("getTaskById[3]: {}", Constants.READ_ERROR);
                    return new Result<>(ResultCode.ERROR, Constants.READ_ERROR);
                });
        }
        catch (Exception exception) {
            logger.error("getTaskById[4]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getTasksByTags(List, UUID)}
     */
    @Override
    public Result<List<Task>> getTasksByTags(List<String> tags, UUID projectId) {
        Result<NoData> checkProjectResult = csvChecker.checkProjectExistence(projectId);
        if (checkProjectResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, checkProjectResult.getMessage());

        try {
            List<Task> taskList = Optional.ofNullable(CsvUtil.readFile(tasksFilePath, Task.class))
                    .map(tasks -> tasks.stream()
                            .filter(task -> task.getProjectId().equals(projectId)).toList())
                    .orElse(new ArrayList<>());

            setTaskTags(taskList);

            return Optional.of(taskList)
                    .map(tasks -> tasks.stream()
                            .filter(task -> !Collections.disjoint(task.getTags(), tags))
                            .collect(Collectors.toList()))
                    .filter(tasks -> !tasks.isEmpty())
                    .map(tasks -> {
                        logger.debug("getTasksByTags[1]: received tasks {}", tasks);
                        return new Result<>(tasks, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        String message = Constants.TASKS_WITH_TAGS_WERE_NOT_FOUND;
                        logger.debug("getTasksByTags[1]: {}", message);
                        return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, message);
                    });
        }
        catch (Exception exception) {
            logger.error("getTaskByTags[3]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getBugReportsByProjectId(UUID)}
     */
    @Override
    public Result<List<BugReport>> getBugReportsByProjectId(UUID projectId) {
        Result<NoData> checkProjectResult = csvChecker.checkProjectExistence(projectId);
        if (checkProjectResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, checkProjectResult.getMessage());

        try {
            List<BugReport> taskList = CsvUtil.readFile(bugReportsFilePath, BugReport.class);
            return Optional.ofNullable(taskList)
                    .map(bugreports -> bugreports.stream()
                            .filter(bugreport -> bugreport.getProjectId().equals(projectId))
                            .toList())
                    .map(bugreports -> {
                        logger.debug("getBugReportsByProjectId[1]: received tasks {}", bugreports);
                        return new Result<>(bugreports, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        String message = String.format("project with id %s has no bug reports", projectId);
                        logger.debug("getBugReportsByProjectId[1]: {}", message);
                        return new Result<>(new ArrayList<>(), ResultCode.SUCCESS, message);
                    });
        }
        catch (Exception exception) {
            logger.error("getBugReportsByProjectId[3]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getBugReportById(UUID)}
     */
    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        try {
            List<BugReport> bugReportList = CsvUtil.readFile(bugReportsFilePath, BugReport.class);
            return Optional.ofNullable(bugReportList)
                    .map(bugReports -> bugReports.stream()
                            .filter(bugReport -> bugReport.getId().equals(bugReportId))
                            .findFirst()
                            .map(bugReport -> {
                                logger.debug("getBugReportById[1]: received bug report {}", bugReport);
                                return new Result<>(bugReport, ResultCode.SUCCESS);
                            })
                            .orElseGet(() -> {
                                logger.debug("getBugReportsById[2]: {}", String.format(
                                        Constants.ENTITY_NOT_FOUND_MESSAGE,
                                        "bug report", bugReportId
                                ));
                                return new Result<>(ResultCode.NOT_FOUND);
                            }))
                    .orElseGet(() -> {
                        logger.error("getBugReportById[1]: {}", Constants.READ_ERROR);
                        return new Result<>(null, ResultCode.ERROR, Constants.READ_ERROR);
                    });
        }
        catch (Exception exception) {
            logger.error("getBugReportById[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getEventsByProjectId(UUID)}
     */
    @Override
    public Result<List<Event>> getEventsByProjectId(UUID projectId) {
        Result<NoData> checkProjectResult = csvChecker.checkProjectExistence(projectId);
        if (checkProjectResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, checkProjectResult.getMessage());

        try {
            List<Event> eventList = Optional.ofNullable(CsvUtil.readFile(eventsFilePath, Event.class))
                    .orElse(new ArrayList<>());

            return Optional.of(eventList)
                    .map(events -> events.stream()
                            .filter(event -> event.getProjectId().equals(projectId))
                            .toList())
                    .map(events -> {
                        List<Event> result = new ArrayList<>(events);
                        logger.debug("getEventsByProjectId[1]: received events {}", result);
                        return new Result<>(result, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        String message = String.format("project with id %s has no events", projectId);
                        logger.error("getEventsByProjectId[]: {}", message);
                        return new Result<>(new ArrayList<>(), ResultCode.SUCCESS, message);
                    });
        }
        catch (Exception exception) {
            logger.error("getEventsByProjectId[]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getEventById(UUID)}
     */
    @Override
    public Result<Event> getEventById(UUID eventId) {
        try {
            List<Event> bugReportList = CsvUtil.readFile(eventsFilePath, Event.class);
            return Optional.ofNullable(bugReportList)
                    .map(events -> events.stream()
                            .filter(event -> event.getId().equals(eventId))
                            .findFirst()
                            .map(event -> {
                                logger.debug("getEventById[1]: received bug report {}", event);
                                return new Result<>(event, ResultCode.SUCCESS);
                            })
                            .orElseGet(() -> {
                                logger.debug("getEventById[2]: {}", String.format(
                                        Constants.ENTITY_NOT_FOUND_MESSAGE,
                                        "bug report", eventId
                                ));
                                return new Result<>(ResultCode.NOT_FOUND);
                            }))
                    .orElseGet(() -> {
                        logger.error("getEventById[1]: {}", Constants.READ_ERROR);
                        return new Result<>(null, ResultCode.ERROR, Constants.READ_ERROR);
                    });
        }
        catch (Exception exception) {
            logger.error("getEventById[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getDocumentationById(UUID)}
     */
    @Override
    public Result<Documentation> getDocumentationById(UUID docId) {
        try {
            List<Documentation> documentationList = CsvUtil.readFile(documentationsFilePath, Documentation.class);
            List<DocumentationData> documentationDataList = Optional.ofNullable(
                    CsvUtil.readFile(documentationDataFilePath, DocumentationData.class)
            )
                    .map(docData -> docData.stream().filter(d -> d.getId().equals(docId)).toList())
                    .orElse(new ArrayList<>());

            return Optional.ofNullable(documentationList)
                    .map(docs -> docs.stream()
                            .filter(doc -> doc.getId().equals(docId))
                            .findFirst()
                            .map(doc -> {
                                HashMap<String, String> body = new HashMap<>();
                                documentationDataList.forEach(docData -> body.put(docData.getArticleTitle(), docData.getArticle()));

                                doc.setBody(body);

                                logger.debug("getDocumentationById[1]: received bug report {}", doc);
                                return new Result<>(doc, ResultCode.SUCCESS);
                            })
                            .orElseGet(() -> {
                                logger.debug("getDocumentationById[2]: {}", String.format(
                                        Constants.ENTITY_NOT_FOUND_MESSAGE,
                                        "bug report", docId
                                ));
                                return new Result<>(ResultCode.NOT_FOUND);
                            }))
                    .orElseGet(() -> {
                        logger.error("getDocumentationById[3]: {}", Constants.READ_ERROR);
                        return new Result<>(null, ResultCode.ERROR, Constants.READ_ERROR);
                    });
        }
        catch (Exception exception) {
            logger.error("getDocumentationById[4]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getDocumentationsByProjectId(UUID)}
     */
    @Override
    public Result<List<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        Result<NoData> checkProjectResult = csvChecker.checkProjectExistence(projectId);
        if (checkProjectResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, checkProjectResult.getMessage());

        try {
            List<Documentation> documentationList = Optional.ofNullable(CsvUtil.readFile(documentationsFilePath, Documentation.class))
                    .orElse(new ArrayList<>());

            List<DocumentationData> documentationDataList = Optional.ofNullable(
                    CsvUtil.readFile(documentationDataFilePath, DocumentationData.class)
            ).orElse(new ArrayList<>());


            documentationList = documentationList.stream().peek(
                    doc -> {
                        HashMap<String, String> body = new HashMap<>();
                        List<DocumentationData> docData = documentationDataList.stream()
                                .filter(data -> data.getId().equals(doc.getId()))
                                .toList();

                        docData.forEach(d -> {
                            body.put(d.getArticleTitle(), d.getArticle());
                            doc.setBody(body);
                        });
                    }
            ).toList();

            return Optional.of(documentationList)
                    .map(docs -> docs.stream()
                            .filter(doc -> doc.getProjectId().equals(projectId))
                            .toList())
                    .map(docs -> {
                        List<Documentation> result = new ArrayList<>(docs);
                        logger.debug("getDocumentationsByProjectId[1]: received documentations {}", result);
                        return new Result<>(result, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        String message = String.format("project with id %s has no documentations", projectId);
                        logger.debug("getDocumentationsByProjectId[2]: {}", message);
                        return new Result<>(new ArrayList<>(), ResultCode.SUCCESS, message);
                    });
        }
        catch (Exception exception) {
            logger.error("getDocumentationsByProjectId[3]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getProjectTeam(UUID)}
     */
    @Override
    public Result<List<Employee>> getProjectTeam(UUID projectId) {
        Result<NoData> checkProjectResult = csvChecker.checkProjectExistence(projectId);
        if (checkProjectResult.getCode() != ResultCode.SUCCESS)
            return new Result<>(new ArrayList<>(), ResultCode.NOT_FOUND, checkProjectResult.getMessage());

        try {
            // filter and get list of employee's id
            List<UUID> employeeLinks = Optional.ofNullable(
                    CsvUtil.readFile(employeeProjectFilePath, EmployeeProjectObject.class)
            )
                    .map(eps -> eps.stream()
                            .filter(ep -> ep.getProjectId().equals(projectId))
                            .map(EmployeeProjectObject::getEmployeeId).toList())
                    .orElse(new ArrayList<>());

            return Optional.ofNullable(CsvUtil.readFile(employeesFilePath, Employee.class))
                    .map(employees -> employees.stream()
                            .filter(e -> employeeLinks.contains(e.getId())).collect(Collectors.toList()))
                    .map(employees -> {
                        List<Employee> result = new ArrayList<>(employees);
                        logger.debug("getProjectTeam[1]: received employees {}", result);
                        return new Result<>(result, ResultCode.SUCCESS);
                    })
                    .orElseGet(() -> {
                        logger.error("getProjectTeam[2]: {}", Constants.READ_ERROR);
                        return new Result<>(ResultCode.ERROR, Constants.READ_ERROR);
                    });
        }
        catch (Exception exception) {
            logger.error("getProjectTeam[2]: {}", exception.getMessage());
            return new Result<>(new ArrayList<>(), ResultCode.ERROR, exception.getMessage());
        }
    }

    /**
     * {@link ru.sfedu.projectmanagement.core.api.DataProvider#getEmployeeById(UUID)}
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        try {
            List<Employee> employees = CsvUtil.readFile(employeesFilePath, Employee.class);
            return Optional.ofNullable(employees)
                    .map(e -> e.stream().filter(employee -> employee.getId().equals(employeeId))
                    .map(employee -> {
                        logger.debug("getEmployeeById[1]: received employee {}", employee);
                        return new Result<>(employee, ResultCode.SUCCESS);
                    }).findFirst()
                    .orElseGet(() -> {
                        logger.debug("getEmployeeById[2]: {}", String.format(
                                Constants.ENTITY_NOT_FOUND_MESSAGE,
                                "employee", employeeId
                        ));
                        return new Result<>(ResultCode.NOT_FOUND);
                    })).orElseGet(() -> {
                        logger.debug("");
                        return new Result<>(ResultCode.NOT_FOUND);
            });
        }
        catch (Exception exception) {
            logger.error("read[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> completeTask(UUID taskId) {
        Result<NoData> result = new Result<>(ResultCode.SUCCESS);
        if (CsvUtil.isRecordNotExists(tasksFilePath, taskId, Task.class))
            return new Result<>(ResultCode.NOT_FOUND, String.format(
                    Constants.ENTITY_NOT_FOUND_MESSAGE,
                    Task.class.getSimpleName(),
                    taskId
            ));

        try {
            List<Task> taskList = Optional.ofNullable(CsvUtil.readFile(tasksFilePath, Task.class))
                    .map(tasks -> tasks
                            .stream()
                            .peek(t -> {
                                if (t.getId().equals(taskId)) t.completeTask();
                            }).toList())
                    .orElse(new ArrayList<>());

            CsvUtil.createRecords(tasksFilePath, taskList, Task.class);
            logger.debug("completeTask[1]: task with id {} was completed", taskId);
        }
        catch (Exception exception) {
            logger.debug("completeTask[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        return result;
    }
}
