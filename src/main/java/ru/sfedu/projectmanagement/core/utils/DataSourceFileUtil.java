package ru.sfedu.projectmanagement.core.utils;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.api.Environment;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.Entity;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import java.io.IOException;

import static ru.sfedu.projectmanagement.core.utils.FileUtil.createFileIfNotExists;

public class DataSourceFileUtil {
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

    public <T extends Entity> boolean createValidation(T entity) {
        return switch (entity.getEntityType()) {
            case BugReport -> createBugReportValidation((BugReport) entity);
            case Documentation -> createDocumentationValidation((Documentation) entity);
            case Employee -> createEmployeeValidation((Employee) entity);
            case Event -> createEventValidation((Event) entity);
            case Project -> createProjectValidation((Project) entity);
            case Task -> createTaskValidation((Task) entity);
            case EmployeeProject -> true;
        };
    }

    private boolean createTaskValidation(Task task) {
        return XmlUtil.isRecordExists(projectsFilePath, task.getProjectId()) &&
                XmlUtil.isRecordExists(employeesFilePath, task.getEmployeeId());
    }

    private boolean createProjectValidation(Project project) {
        return true;
    }

    private boolean createBugReportValidation(BugReport bugReport) {
        return true;
    }

    private boolean createDocumentationValidation(Documentation documentation) {
        return true;
    }

    private boolean createEmployeeValidation(Employee employee) {
        return true;
    }

    private boolean createEventValidation(Event event) {
        return true;
    }
}
