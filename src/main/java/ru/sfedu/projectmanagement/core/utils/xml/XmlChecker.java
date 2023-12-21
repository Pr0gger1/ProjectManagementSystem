package ru.sfedu.projectmanagement.core.utils.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Employee;
import ru.sfedu.projectmanagement.core.model.Project;
import ru.sfedu.projectmanagement.core.model.ProjectEntity;
import ru.sfedu.projectmanagement.core.utils.FileChecker;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.util.TreeMap;
import java.util.UUID;

public class XmlChecker extends FileChecker {
    private final Logger logger = LogManager.getLogger(XmlChecker.class);

    public XmlChecker(
            String projectsFilePath,
            String employeesFilePath,
            String tasksFilePath,
            String bugReportsFilePath,
            String eventsFilePath,
            String documentationsFilePath,
            String employeeProjectFilePath
    ) {
        super(projectsFilePath, employeesFilePath, tasksFilePath, bugReportsFilePath, eventsFilePath, documentationsFilePath, employeeProjectFilePath);
    }

    @Override
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


    @Override
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

    @Override
    protected Result<NoData> createProjectValidation(Project project) {
        logger.debug("createProjectValidation[1]: creating project {}", project);
        TreeMap<String, String> errors = new TreeMap<>();

        if (project.getManagerId() != null && !XmlUtil.isRecordExists(employeesFilePath, project.getManagerId())) {
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, project.getManagerId()));
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("createProjectValidation[2]: is valid: true");
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
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

    @Override
    public Result<NoData> checkIfEmployeeBelongsToProject(UUID employeeId, UUID projectId) {
        return null;
    }
}
