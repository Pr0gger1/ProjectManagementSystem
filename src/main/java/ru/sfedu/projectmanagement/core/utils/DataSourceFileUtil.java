package ru.sfedu.projectmanagement.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.api.Environment;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.Entity;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.xml.Wrapper;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFileIfNotExists;

public class DataSourceFileUtil {
    private static final Logger logger = LogManager.getLogger(DataSourceFileUtil.class);
    public final String projectsFilePath;
    public final String employeesFilePath;
    public final String tasksFilePath;
    public final String bugReportsFilePath;
    public final String eventsFilePath;
    public final String documentationsFilePath;
    public final String employeeProjectFilePath;
    public final String actualDatasourcePath;


    public DataSourceFileUtil(DataSourceType type) {
        Environment environment = Environment.valueOf(
                ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT)
        );

        if (environment == Environment.PRODUCTION) {
            actualDatasourcePath = (type == DataSourceType.CSV) ?
                    Constants.DATASOURCE_PATH_CSV : Constants.DATASOURCE_PATH_XML;
        } else {
            actualDatasourcePath = (type == DataSourceType.XML) ?
                    Constants.DATASOURCE_TEST_PATH_XML : Constants.DATASOURCE_TEST_PATH_CSV;
        }

        projectsFilePath = actualDatasourcePath
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        employeesFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEES_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        tasksFilePath = actualDatasourcePath
                .concat(Constants.TASKS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        bugReportsFilePath = actualDatasourcePath
                .concat(Constants.BUG_REPORTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        eventsFilePath = actualDatasourcePath
                .concat(Constants.EVENTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        documentationsFilePath = actualDatasourcePath
                .concat(Constants.DOCUMENTATIONS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);

        employeeProjectFilePath = actualDatasourcePath
                .concat(Constants.EMPLOYEE_PROJECT_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION);
    }

    public void createDatasourceFiles() throws IOException {
        String[] paths = {
            projectsFilePath,
            employeesFilePath,
            employeeProjectFilePath,
            tasksFilePath,
            bugReportsFilePath,
            eventsFilePath,
            documentationsFilePath
        };

        for (String path : paths) {
            createFileIfNotExists(path);
        }
    }

    public <T extends Entity> Result<NoData> createValidation(T entity) {
        logger.debug("Entity type: " + entity.getClass().getSimpleName());
        logger.debug("Expected type: " + entity.getEntityType());
        return switch (entity.getEntityType()) {
            case BugReport -> createProjectEntityValidation((BugReport) entity);
            case Documentation -> createProjectEntityValidation((Documentation) entity);
            case Employee -> createEmployeeValidation((Employee) entity);
            case Event -> createProjectEntityValidation((Event) entity);
            case Project -> createProjectValidation((Project) entity);
            case Task -> createProjectEntityValidation((Task) entity);
            default -> throw new IllegalArgumentException("Unsupported entity type: " + entity.getEntityType());
        };
    }

    public <T extends Entity> Result<NoData> checkProjectAndEmployeeExistence(String filePath, UUID id) {
        Wrapper<T> entity = XmlUtil.read(filePath);
        return entity.getList()
                .stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .map(e -> checkProjectAndEmployeeExistence((ProjectEntity) e))
                .orElse(new Result<>(null,  ResultCode.ERROR, new TreeMap<>(Map.of("entity", "couldn't find an entity"))));
    }
    public Result<NoData> checkProjectAndEmployeeExistence(ProjectEntity entity) {
        logger.debug("checkProjectAndEmployeeExistence[1]: creating {} {}", entity.getClass().getSimpleName(), entity);
        TreeMap<String, String> errors = new TreeMap<>();
        if (!XmlUtil.isRecordExists(employeesFilePath, entity.getEmployeeId()))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, entity.getEmployeeId()));
        if (!XmlUtil.isRecordExists(projectsFilePath, entity.getProjectId()))
            errors.put(Constants.PROJECT_ERROR_KEY, String.format(Constants.PROJECT_DOES_NOT_EXISTS, entity.getProjectId()));
        if (!XmlUtil.isRecordExists(employeeProjectFilePath, entity.getEmployeeId()))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT);

        if (!errors.isEmpty()) {
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("checkProjectAndEmployeeExistence[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    private Result<NoData> createProjectEntityValidation(ProjectEntity entity) {
        logger.debug("createProjectEntityValidation[1]: creating {} {}", entity.getClass().getSimpleName(), entity);
        TreeMap<String, String> errors = new TreeMap<>();
        Result<NoData> result = checkProjectAndEmployeeExistence(entity);

        if (entity.getName().isEmpty())
            errors.put(Constants.BUG_REPORT_ERROR_KEY, Constants.ENTITY_INVALID_NAME);
        if (entity.getEmployeeId() == null)
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_IS_NULL);
        if (entity.getEmployeeFullName().isEmpty())
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_INVALID_FULL_NAME);

        if (!errors.isEmpty() || !result.getErrors().isEmpty()) {
            result.setCode(ResultCode.INVALID_DATA);
            result.addError(errors);
        }

        logger.info("createProjectEntityValidation[2]: is valid: {}", true);
        return result;
    }

    public Result<NoData> checkEntitiesBeforeBindTaskExecutor(UUID executorId, UUID taskId, UUID projectId) {
        logger.debug("checkEntitiesBeforeBindTaskExecutor[1]: start validating");
        TreeMap<String, String> errors = new TreeMap<>();

        if (!XmlUtil.isRecordExists(tasksFilePath, taskId))
            errors.put(Constants.TASK_ERROR_KEY, Constants.TASK_DOES_NOT_EXISTS);
        if (!XmlUtil.isRecordExists(employeesFilePath, executorId))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);
        if (!XmlUtil.isRecordExists(projectsFilePath, projectId))
            errors.put(Constants.PROJECT_ERROR_KEY, Constants.PROJECT_DOES_NOT_EXISTS);
        if (!XmlUtil.isRecordExists(employeeProjectFilePath, executorId))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.ERROR, errors);

        logger.info("checkEntitiesBeforeBindTaskExecutor[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    private Result<NoData> createProjectValidation(Project project) {
        logger.debug("createProjectValidation[1]: creating project {}", project);
        TreeMap<String, String> errors = new TreeMap<>();

        if (project.getManager() != null && !XmlUtil.isRecordExists(employeesFilePath, project.getManagerId())) {
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, project.getManagerId()));
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("createProjectValidation[2]: is valid: true");
        return new Result<>(ResultCode.SUCCESS);
    }

    private Result<NoData> createEmployeeValidation(Employee employee) {
        logger.debug("createEmployeeValidation[1]: creating employee {}", employee);
        TreeMap<String, String> errors = new TreeMap<>();
        if (employee.getFirstName().isEmpty())
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_INVALID_NAME);
        if (employee.getLastName().isEmpty())
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_INVALID_LAST_NAME);
        if (employee.getPosition().isEmpty())
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_INVALID_POSITION);

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.INVALID_DATA, errors);

        return new Result<>(ResultCode.SUCCESS);
    }

    public Result<NoData> checkIfEmployeeBelongsToProject(Employee employee) {
        logger.debug("checkIfEmployeeBelongsToProject[1]: object {}", employee);
        TreeMap<String, String> errors = new TreeMap<>();
        if (!XmlUtil.isRecordExists(employeesFilePath, employee.getId()))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);
        if (!XmlUtil.isRecordExists(employeeProjectFilePath, employee.getId()))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT);

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.ERROR, errors);

        logger.info("checkIfEmployeeBelongsToProject[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

}
