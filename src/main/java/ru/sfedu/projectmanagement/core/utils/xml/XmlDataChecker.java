package ru.sfedu.projectmanagement.core.utils.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.EmployeeProjectObject;
import ru.sfedu.projectmanagement.core.model.Project;
import ru.sfedu.projectmanagement.core.model.ProjectEntity;
import ru.sfedu.projectmanagement.core.utils.FileDataChecker;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

public class XmlDataChecker extends FileDataChecker {
    private final Logger logger = LogManager.getLogger(XmlDataChecker.class);

    public XmlDataChecker(
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
        if (XmlUtil.isRecordNotExists(employeesFilePath, entity.getEmployeeId()))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, entity.getEmployeeId()));
        if (XmlUtil.isRecordNotExists(projectsFilePath, entity.getProjectId()))
            errors.put(Constants.PROJECT_ERROR_KEY, String.format(Constants.PROJECT_DOES_NOT_EXISTS, entity.getProjectId()));
        if (XmlUtil.isRecordNotExists(employeeProjectFilePath, entity.getEmployeeId()))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT);

        if (!errors.isEmpty()) {
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("checkProjectAndEmployeeExistence[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    public Result<NoData> checkProjectAndEmployeeExistence(UUID employeeId, UUID projectId) {
        TreeMap<String, String> errors = new TreeMap<>();
        if (XmlUtil.isRecordNotExists(employeesFilePath, employeeId))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, employeeId));
        if (XmlUtil.isRecordNotExists(projectsFilePath, projectId))
            errors.put(Constants.PROJECT_ERROR_KEY, String.format(Constants.PROJECT_DOES_NOT_EXISTS, projectId));

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

        if (XmlUtil.isRecordNotExists(tasksFilePath, taskId))
            errors.put(Constants.TASK_ERROR_KEY, Constants.TASK_DOES_NOT_EXISTS);
        if (XmlUtil.isRecordNotExists(employeesFilePath, executorId))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);
        if (XmlUtil.isRecordNotExists(projectsFilePath, projectId))
            errors.put(Constants.PROJECT_ERROR_KEY, Constants.PROJECT_DOES_NOT_EXISTS);
        if (XmlUtil.isRecordNotExists(employeeProjectFilePath, executorId))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, Constants.EMPLOYEE_DOES_NOT_EXISTS);

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.ERROR, errors);

        logger.info("checkEntitiesBeforeBindTaskExecutor[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
    protected Result<NoData> createProjectConstraint(Project project) {
        logger.debug("createProjectValidation[1]: creating project {}", project);
        TreeMap<String, String> errors = new TreeMap<>();

        if (project.getManager() != null && !XmlUtil.isRecordNotExists(employeesFilePath, project.getManager().getId())) {
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, project.getManager()));
            return new Result<>(null, ResultCode.ERROR, errors);
        }

        logger.info("createProjectValidation[2]: is valid: true");
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
    public Result<NoData> checkIfEmployeeBelongsToProject(UUID employeeId, UUID projectId) {
        logger.debug("checkIfEmployeeBelongsToProject[1]: object {}", employeeId);
        TreeMap<String, String> errors = new TreeMap<>();
        if (XmlUtil.isRecordNotExists(employeesFilePath, employeeId))
            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_DOES_NOT_EXISTS, employeeId));

        Wrapper<EmployeeProjectObject> employeeLinks = XmlUtil.readFile(employeeProjectFilePath);
        Optional.of(employeeLinks.getList())
                .map(links -> links
                        .stream()
                        .filter(link -> link.getEmployeeId().equals(employeeId) && link.getProjectId().equals(projectId))
                        .findAny()
                        .orElseGet(() -> {
                            errors.put(Constants.EMPLOYEE_ERROR_KEY, String.format(Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT, employeeId));
                            return null;
                        })
                );

        if (!errors.isEmpty())
            return new Result<>(null, ResultCode.ERROR, errors);

        logger.info("checkIfEmployeeBelongsToProject[2]: is valid: {}", true);
        return new Result<>(ResultCode.SUCCESS);
    }

    @Override
    public Result<NoData> checkProjectExistence(UUID projectId) {
        if (XmlUtil.isRecordNotExists(projectsFilePath, projectId))
            return new Result<>(ResultCode.ERROR, String.format(
                    Constants.ENTITY_NOT_FOUND_MESSAGE,
                    Project.class.getSimpleName(),
                    projectId
            ));
        return new Result<>(ResultCode.SUCCESS);
    }
}
