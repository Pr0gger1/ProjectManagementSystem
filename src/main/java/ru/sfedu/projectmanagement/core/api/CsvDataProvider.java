package ru.sfedu.projectmanagement.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.ChangeType;
import ru.sfedu.projectmanagement.core.utils.DataSourceFileUtil;
import ru.sfedu.projectmanagement.core.utils.DataSourceType;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.csv.CsvUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFolderIfNotExists;

public class CsvDataProvider extends DataProvider {
    private final DataSourceFileUtil dataSourceFileUtil = new DataSourceFileUtil(DataSourceType.CSV);
    private final Logger logger = LogManager.getLogger(CsvDataProvider.class);

    public CsvDataProvider() {
        try {
            createFolderIfNotExists(dataSourceFileUtil.actualDatasourcePath);
            dataSourceFileUtil.createDatasourceFiles();
        }
        catch (IOException exception) {
            logger.error("CsvDataProvider[1]: Database initialization error: {}", exception.getMessage());
        }
    }

    @Override
    public Result<NoData> processNewProject(Project project) {
        try {
            Result<NoData> result = new Result<>(ResultCode.SUCCESS);
            CsvUtil.createRecord(dataSourceFileUtil.projectsFilePath, project, Project.class);
            Result<NoData> initResult = initProjectEntities(project);

            if (initResult.getCode() != ResultCode.SUCCESS)
                return initResult;


            logger.info("processNewProject[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", project.getId()
            ));
            return result;
        }
        catch (Exception exception) {
            logger.error("processNewProject[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> processNewTask(Task task) {
        try {
            Result<NoData> result = new Result<>(ResultCode.SUCCESS);
            CsvUtil.createRecord(dataSourceFileUtil.tasksFilePath, task, Task.class);

            logger.info("processNewTask[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", task.getId()
            ));

            return result;
        }
        catch (Exception exception) {
            logger.error("processNewTask[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> processNewBugReport(BugReport bugReport) {
        try {
            Result<NoData> result = new Result<>(ResultCode.SUCCESS);
            CsvUtil.createRecord(dataSourceFileUtil.bugReportsFilePath, bugReport, BugReport.class);

            logger.info("processNewBugReport[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", bugReport.getId()
            ));

            return result;
        }
        catch (Exception exception) {
            logger.error("processNewBugReport[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> processNewDocumentation(Documentation documentation) {
        try {
            Result<NoData> result = new Result<>(ResultCode.SUCCESS);
            CsvUtil.createRecord(dataSourceFileUtil.documentationsFilePath, documentation, Documentation.class);

            logger.info("processNewDocumentation[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", documentation.getId()
            ));

            return result;
        }
        catch (Exception exception) {
            logger.error("processNewDocumentation[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> processNewEvent(Event event) {
        try {
            Result<NoData> result = new Result<>(ResultCode.SUCCESS);
            CsvUtil.createRecord(dataSourceFileUtil.eventsFilePath, event, Event.class);

            logger.info("processNewEvent[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", event.getId()
            ));

            return result;
        }
        catch (Exception exception) {
            logger.error("processNewEvent[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<NoData> processNewEmployee(Employee employee) {
        try {
            CsvUtil.createRecord(dataSourceFileUtil.employeesFilePath, employee, Employee.class);
            logger.info("processNewEmployee[1]: {}", String.format(
                    Constants.SUCCESSFUL_CREATED_ENTITY_MESSAGE,
                    "employee", employee.getId()
            ));
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (Exception exception) {
            logger.error("createRecord[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public ProjectStatistics monitorProjectCharacteristics(
            UUID projectId, boolean checkLaborEfficiency, boolean trackBugs
    ) {
        return null;
    }

    @Override
    public float calculateProjectReadiness(UUID projectId) {
        return 0;
    }

    @Override
    public TrackInfo<Employee, Float> calculateLaborEfficiency(UUID projectId) {
        return null;
    }

    @Override
    public TrackInfo<Task, String> trackTaskStatus(UUID projectId) {
        return null;
    }

    @Override
    public TrackInfo<BugReport, String> trackBugReportStatus(UUID projectId) {
        return null;
    }


    @Override
    public Result<NoData> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        return null;
    }

    @Override
    public Result<NoData> bindProjectManager(UUID managerId, UUID projectId) {
        return null;
    }

    @Override
    public Result<NoData> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, UUID projectId) {
        return null;
    }

    @Override
    public Result<NoData> deleteProject(UUID projectId) {
        AtomicReference<Project> projectBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Project> data = CsvUtil.readFile(dataSourceFileUtil.projectsFilePath, Project.class);
            data = data.stream().filter(project -> {
                if (project.getId().equals(projectId)) {
                    result.setCode(ResultCode.SUCCESS);
                    projectBean.set(project);
                    return false;
                }
                return true;
            }).toList();
            CsvUtil.createRecords(dataSourceFileUtil.projectsFilePath, data, Project.class);

            logger.info("deleteProject[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "project", projectId
            ));
            return new Result<>(ResultCode.SUCCESS);
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


    @Override
    public Result<NoData> deleteTask(UUID taskId) {
        AtomicReference<Task> taskBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Task> data = CsvUtil.readFile(dataSourceFileUtil.tasksFilePath, Task.class);
            data = data.stream().filter(task -> {
                if(task.getId().equals(taskId)) {
                    result.setCode(ResultCode.SUCCESS);
                    taskBean.set(task);
                    return false;
                }
                return true;
            }).toList();

            CsvUtil.createRecords(dataSourceFileUtil.tasksFilePath, data, Task.class);

            logger.info("deleteTask[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", taskId
            ));
            return new Result<>(ResultCode.SUCCESS);
        }
        catch (Exception exception) {
            logger.debug("deleteTask[2]: {}", exception.getMessage());
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

    @Override
    public Result<NoData> deleteBugReport(UUID bugReportId) {
        AtomicReference<BugReport> bugReportBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<BugReport> data = CsvUtil.readFile(dataSourceFileUtil.bugReportsFilePath, BugReport.class);
            data = data.stream().filter(bugReport -> {
                if(bugReport.getId().equals(bugReportId)) {
                    result.setCode(ResultCode.SUCCESS);
                    bugReportBean.set(bugReport);
                    return false;
                }
                return true;
            }).toList();

            CsvUtil.createRecords(dataSourceFileUtil.bugReportsFilePath, data, BugReport.class);

            logger.info("deleteBugReport[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", bugReportId
            ));
            return new Result<>(ResultCode.SUCCESS);
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

    @Override
    public Result<NoData> deleteEvent(UUID eventId) {
        AtomicReference<Event> eventBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Event> data = CsvUtil.readFile(dataSourceFileUtil.eventsFilePath, Event.class);
            data = data.stream().filter(event -> {
                if(event.getId().equals(eventId)) {
                    result.setCode(ResultCode.SUCCESS);
                    eventBean.set(event);
                    return false;
                }
                return true;
            }).toList();

            CsvUtil.createRecords(dataSourceFileUtil.eventsFilePath, data, Event.class);

            logger.info("deleteEvent[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", eventId
            ));
            return new Result<>(ResultCode.SUCCESS);
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

    @Override
    public Result<NoData> deleteDocumentation(UUID docId) {
        AtomicReference<Documentation> docBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Documentation> data = CsvUtil.readFile(dataSourceFileUtil.documentationsFilePath, Documentation.class);
            data = data.stream().filter(doc -> {
                if(doc.getId().equals(docId)) {
                    result.setCode(ResultCode.SUCCESS);
                    docBean.set(doc);
                    return false;
                }
                return true;
            }).toList();

            CsvUtil.createRecords(dataSourceFileUtil.documentationsFilePath, data, Documentation.class);

            logger.info("deleteDocumentation[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", docId
            ));
            return new Result<>(ResultCode.SUCCESS);
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

    @Override
    public Result<NoData> deleteEmployee(UUID employeeId) {
        AtomicReference<Employee> employeeBean = new AtomicReference<>(null);
        Result<NoData> result = new Result<>(ResultCode.NOT_FOUND);

        try {
            List<Employee> data = CsvUtil.readFile(dataSourceFileUtil.employeesFilePath, Employee.class);
            data = data.stream().filter(task -> {
                if(task.getId().equals(employeeId)) {
                    result.setCode(ResultCode.SUCCESS);
                    employeeBean.set(task);
                    return false;
                }
                return true;
            }).toList();

            CsvUtil.createRecords(dataSourceFileUtil.employeesFilePath, data, Employee.class);

            logger.info("deleteEmployee[1]: {}", String.format(
                    Constants.SUCCESSFUL_DELETED_ENTITY_MESSAGE,
                    "task", employeeId
            ));
            return new Result<>(ResultCode.SUCCESS);
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

    @Override
    public Result<Project> getProjectById(UUID id) {
        try {
            List<Project> data = CsvUtil.readFile(dataSourceFileUtil.projectsFilePath, Project.class);
            return new Result<>(data.get(0), ResultCode.SUCCESS);
        }
        catch (Exception exception) {
            logger.error("getProjectById[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }

    @Override
    public Result<ArrayList<Task>> getTasksByProjectId(UUID projectId) {
        return null;
    }

    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) { return null; }

    @Override
    public Result<Task> getTaskById(UUID taskId) {
        return null;
    }

    @Override
    public Result<ArrayList<Task>> getTasksByTags(ArrayList<String> tags, UUID projectId) {
        return null;
    }

    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(UUID projectId) {
        return null;
    }


    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        return null;
    }

    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(UUID projectId) {
        return null;
    }

    @Override
    public Result<Event> getEventById(UUID eventId) {
        return null;
    }

    @Override
    public Result<Documentation> getDocumentationById(UUID docId) { return null; }


    @Override
    public Result<ArrayList<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        return null;
    }

    @Override
    public Result<ArrayList<Employee>> getProjectTeam(UUID projectId) {
        return null;
    }

    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        try {
            List<Employee> employees = CsvUtil.readFile(dataSourceFileUtil.employeesFilePath, Employee.class);
            return employees.stream().filter(employee -> employee.getId().equals(employeeId))
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
                    });
        }
        catch (Exception exception) {
            logger.error("read[2]: {}", exception.getMessage());
            return new Result<>(ResultCode.ERROR, exception.getMessage());
        }
    }
}
