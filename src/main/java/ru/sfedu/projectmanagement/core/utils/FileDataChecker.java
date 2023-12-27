package ru.sfedu.projectmanagement.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.util.TreeMap;
import java.util.UUID;

abstract public class FileDataChecker {
    private final Logger logger = LogManager.getLogger(FileDataChecker.class);
    protected final String projectsFilePath;
    protected final String employeesFilePath;
    protected final String tasksFilePath;
    protected final String bugReportsFilePath;
    protected final String eventsFilePath;
    protected final String documentationsFilePath;
    protected final String employeeProjectFilePath;

    protected FileDataChecker(
            String projectsFilePath,
            String employeesFilePath,
            String tasksFilePath,
            String bugReportsFilePath,
            String eventsFilePath,
            String documentationsFilePath,
            String employeeProjectFilePath
    ) {
        this.employeeProjectFilePath = employeeProjectFilePath;
        this.tasksFilePath = tasksFilePath;
        this.bugReportsFilePath = bugReportsFilePath;
        this.eventsFilePath = eventsFilePath;
        this.documentationsFilePath = documentationsFilePath;
        this.projectsFilePath = projectsFilePath;
        this.employeesFilePath = employeesFilePath;
    }


     public <T extends Entity> Result<NoData> checkBeforeCreate(T entity) {
        logger.debug("Entity type: " + entity.getClass().getSimpleName());
        logger.debug("Expected type: " + entity.getEntityType());
        return switch (entity.getEntityType()) {
            case BugReport -> createProjectEntityConstraint((BugReport) entity);
            case Documentation -> createProjectEntityConstraint((Documentation) entity);
            case Employee -> createEmployeeConstraint((Employee) entity);
            case Event -> createProjectEntityConstraint((Event) entity);
            case Project -> createProjectConstraint((Project) entity);
            case Task -> createProjectEntityConstraint((Task) entity);
            default -> throw new IllegalArgumentException("Unsupported entity type: " + entity.getEntityType());
        };
    }

    abstract public Result<NoData> checkProjectAndEmployeeExistence(ProjectEntity entity);

    protected Result<NoData> createProjectEntityConstraint(ProjectEntity entity) {
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

    abstract public Result<NoData> checkEntitiesBeforeBindTaskExecutor(UUID executorId, UUID taskId, UUID projectId);
    abstract public Result<NoData> checkProjectAndEmployeeExistence(UUID employeeId, UUID projectId);

    abstract  protected Result<NoData> createProjectConstraint(Project project);
    protected Result<NoData> createEmployeeConstraint(Employee employee) {
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

    abstract public Result<NoData> checkIfEmployeeBelongsToProject(UUID employeeId, UUID projectId);
    abstract public Result<NoData> checkProjectExistence(UUID projectId);
}
