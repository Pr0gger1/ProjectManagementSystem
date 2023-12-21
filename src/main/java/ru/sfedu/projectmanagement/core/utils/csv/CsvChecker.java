package ru.sfedu.projectmanagement.core.utils.csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.utils.FileChecker;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import static ru.sfedu.projectmanagement.core.utils.csv.CsvUtil.readFile;

public class CsvChecker extends FileChecker {
    private final Logger logger = LogManager.getLogger(CsvChecker.class);

    public CsvChecker(
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
        if (CsvUtil.isRecordNotExists(employeesFilePath, entity.getEmployeeId(), Employee.class))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, entity.getEmployeeId()));
        if (CsvUtil.isRecordNotExists(projectsFilePath, entity.getProjectId(), Project.class))
            errors.put(Constants.PROJECT_ERROR_KEY, String.format(Constants.PROJECT_DOES_NOT_EXISTS, entity.getProjectId()));

        List<EmployeeProjectObject> data = readFile(employeeProjectFilePath, EmployeeProjectObject.class);
        boolean isEmployeeLinkExists = !Optional.ofNullable(data)
                .map(d -> d.stream().anyMatch(e -> e.getId().equals(entity.getEmployeeId())))
                .orElse(false);

        if (!isEmployeeLinkExists)
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT);

        if (!errors.isEmpty()) {
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("checkProjectAndEmployeeExistence[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
    public Result<NoData> checkEntitiesBeforeBindTaskExecutor(
            UUID executorId, UUID taskId, UUID projectId
    ) {
        logger.debug("checkEntitiesBeforeBindTaskExecutor[1]: start validating");
        TreeMap<String, String> errors = new TreeMap<>();

        if (CsvUtil.isRecordNotExists(tasksFilePath, taskId, Task.class))
            errors.put(Constants.TASK_ERROR_KEY, Constants.TASK_DOES_NOT_EXISTS);
        if (CsvUtil.isRecordNotExists(employeesFilePath, executorId, Employee.class))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);
        if (CsvUtil.isRecordNotExists(projectsFilePath, projectId, Project.class))
            errors.put(Constants.PROJECT_ERROR_KEY, Constants.PROJECT_DOES_NOT_EXISTS);
        if (CsvUtil.isRecordNotExists(employeeProjectFilePath, executorId, EmployeeProjectObject.class))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.ERROR, errors);

        logger.info("checkEntitiesBeforeBindTaskExecutor[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
    public Result<NoData> createProjectValidation(Project project) {
        logger.debug("createProjectValidation[1]: creating project {}", project);
        TreeMap<String, String> errors = new TreeMap<>();

        if (project.getManagerId() != null && CsvUtil.isRecordNotExists(employeesFilePath, project.getManagerId(), Employee.class)) {
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, project.getManagerId()));
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("createProjectValidation[2]: is valid: true");
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
    public Result<NoData> checkIfEmployeeBelongsToProject(Employee employee) {
        return null;
    }

    @Override
    public Result<NoData> checkIfEmployeeBelongsToProject(UUID employeeId, UUID projectId) {
        logger.debug("checkIfEmployeeBelongsToProject[1]: object {}", employeeId);
        TreeMap<String, String> errors = new TreeMap<>();
        if (CsvUtil.isRecordNotExists(employeesFilePath, employeeId, Employee.class))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);
        if (CsvUtil.isRecordNotExists(employeeProjectFilePath, projectId, EmployeeProjectObject.class))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT);

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.ERROR, errors);

        logger.info("checkIfEmployeeBelongsToProject[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }
}
