package ru.sfedu.projectmanagement.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.api.CsvDataProvider;
import ru.sfedu.projectmanagement.core.api.DataProvider;
import ru.sfedu.projectmanagement.core.api.PostgresDataProvider;
import ru.sfedu.projectmanagement.core.api.XmlDataProvider;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.CliUtils;
import ru.sfedu.projectmanagement.core.utils.config.ConfigPropertiesUtil;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final CommandLineParser commandLineParser = new DefaultParser();
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static DataProvider provider = null;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");


    public static void main(String[] args) {
        String log4jConfigPath = System.getProperty(CliConstants.SYSTEM_VAR_LOG4J_CONFIG_PATH);
        if (log4jConfigPath != null) {
            File file = new File(log4jConfigPath);
            logger.info("Main[1]: текущий путь до конфигурации log4j2 {}", file.getAbsolutePath());
        }

        try {
            CommandLine cmd = commandLineParser.parse(CliUtils.getAllOptions(), args);
            initPropertiesConfig(cmd);
            chooseDatasourceOption(cmd);

            createProjectOption(cmd);
            createEmployeeOption(cmd);
            createEventOption(cmd);
            createBugReportOption(cmd);
            createDocumentationOption(cmd);
            createTaskOption(cmd);

            bindEmployeeToProjectOption(cmd);

            deleteProjectOption(cmd);
            deleteEmployeeOption(cmd);
            deleteEventOption(cmd);
            deleteBugReportOption(cmd);
            deleteDocumentationOption(cmd);
            deleteTaskOption(cmd);

            getTaskOption(cmd);
            getProjectOption(cmd);
            getTasksByProjectIdOption(cmd);
            getTasksByEmployeeIdOption(cmd);
            getTasksByTagsOption(cmd);

            getBugReportOption(cmd);
            getBugReportsByProjectIdOption(cmd);

            getDocumentationOption(cmd);
            getDocumentationsByProjectIdOption(cmd);

            getEventOption(cmd);
            getEventsByProjectIdOption(cmd);

            getEmployeeOption(cmd);
            getProjectTeamOption(cmd);
            helpOption(cmd);
            projectStatsOption(cmd);
            completeTaskOption(cmd);
        }
        catch (ParseException | NullPointerException | IllegalArgumentException | DateTimeParseException e) {
            logger.error("Ошибка: {}", e.getMessage());
        }
    }


    private static <T extends Entity> void printResultData(T result) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(result);
            logger.info("printResultData[1]: полученные данные: \n{}", json);
        }
        catch (JsonProcessingException e) {
            logger.error("printResultData[2]: {}", e.getMessage());
        }
    }

    private static void chooseDatasourceOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DATASOURCE_TYPE_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DATASOURCE_TYPE_OPTION);
            switch (arguments[0]) {
                case "csv" -> provider = new CsvDataProvider();
                case "xml" -> provider = new XmlDataProvider();
                case "postgres" -> provider = new PostgresDataProvider();
                default -> logger.error("Main[3]: Выбран несуществующий источник данных");
            }
        }
    }

    public static void initPropertiesConfig(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_ENVIRONMENT_PROPERTIES)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_ENVIRONMENT_PROPERTIES);

            ConfigPropertiesUtil.setConfigPath(arguments[0]);
            File propertiesFile = new File(ConfigPropertiesUtil.getConfigPath());
            logger.debug("initPropertiesConfig[1]: текущий путь до файла конфигурации properties {}", propertiesFile.getAbsolutePath());
        }
    }

    private static void helpOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_HELP_OPTION)) {
            String commandLineSyntax = "java [-Dlog4j.configurationFile='file_path'] -jar project-management-core.jar";
            PrintWriter printWriter = new PrintWriter(System.out);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(
                    printWriter,
                    150,
                    commandLineSyntax,
                    "Options",
                    CliUtils.getAllOptions(),
                    5, 5,
                    "Дата задается в формате yyyy.MM.dd и yyyy.MM.dd HH:mm",
                    true
            );
            printWriter.flush();
        }
    }

    private static void createProjectOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_CREATE_PROJECT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_CREATE_PROJECT_OPTION);

            Employee manager = arguments[2].equals("null") ? null : provider.getEmployeeById(UUID.fromString(arguments[2])).getData();
            Project project = new Project();
            project.setManager(manager);
            project.setName(arguments[0]);
            project.setDescription(arguments[1]);
            project.setStatus(WorkStatus.valueOf(arguments[3].toUpperCase()));
            project.setDeadline(LocalDateTime.parse(arguments[4], dateFormatter));

            Result<NoData> result = provider.processNewProject(project);
            logger.info("createProjectOption[1]: статус создания проекта {}", result);
        }
    }

    private static void createEmployeeOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_CREATE_EMPLOYEE_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_CREATE_EMPLOYEE_OPTION);

            Employee employee = new Employee();
            employee.setFirstName(arguments[0]);
            employee.setLastName(arguments[1]);
            employee.setPatronymic(arguments[2].equals("null") ? null : arguments[2]);
            employee.setEmail(arguments[3]);
            employee.setBirthday(LocalDate.parse(arguments[4], dateFormatter));
            employee.setPhoneNumber(arguments[5]);
            employee.setPosition(arguments[6]);

            Result<NoData> result = provider.processNewEmployee(employee);
            logger.info("createEmployeeOption[1]: статус создания сотрудника {}", result);
        }
    }

    private static void createEventOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_CREATE_EVENT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_CREATE_EVENT_OPTION);

            Event event = new Event();
            event.setName(arguments[0]);
            event.setDescription(arguments[1]);
            event.setProjectId(UUID.fromString(arguments[2]));
            event.setEmployeeId(UUID.fromString(arguments[3]));
            event.setEmployeeFullName(arguments[4]);
            event.setStartDate(LocalDateTime.parse(arguments[5], dateTimeFormatter));
            event.setEndDate(LocalDateTime.parse(arguments[6], dateTimeFormatter));

            Result<NoData> result = provider.processNewEvent(event);
            logger.info("createEventOption[1]: статус создания события проекта {}", result);
        }
    }

    private static void createBugReportOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_CREATE_BUG_REPORT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_CREATE_BUG_REPORT_OPTION);

            BugReport bugReport = new BugReport();
            bugReport.setName(arguments[0]);
            bugReport.setDescription(arguments[1]);
            bugReport.setStatus(BugStatus.valueOf(arguments[2].toUpperCase()));
            bugReport.setPriority(Priority.valueOf(arguments[3].toUpperCase()));
            bugReport.setProjectId(UUID.fromString(arguments[4]));
            bugReport.setEmployeeId(UUID.fromString(arguments[5]));
            bugReport.setEmployeeFullName(arguments[6]);

            Result<NoData> result = provider.processNewBugReport(bugReport);
            logger.info("createBugReportOption[1]: статус создания баг репорта проекта {}", result);
        }
    }

    private static void createDocumentationOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_CREATE_DOCUMENTATION_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_CREATE_DOCUMENTATION_OPTION);
            String[] docArguments = new String[0];
            if (cmd.hasOption(CliConstants.CLI_DOC_DATA_OPTION)) {
                docArguments = cmd.getOptionValues(CliConstants.CLI_DOC_DATA_OPTION);
            }

            HashMap<String, String> docBody = new HashMap<>();
            if (docArguments.length != 0) docBody = CliUtils.parseDocBody(docArguments);

            Documentation documentation = new Documentation();
            documentation.setName(arguments[0]);
            documentation.setDescription(arguments[1]);
            documentation.setProjectId(UUID.fromString(arguments[2]));
            documentation.setEmployeeId(UUID.fromString(arguments[3]));
            documentation.setEmployeeFullName(arguments[4]);
            documentation.setBody(docBody);

            Result<NoData> result = provider.processNewDocumentation(documentation);
            logger.info("createDocumentationOption[1]: статус создания документации {}", result);
        }
    }

    private static void createTaskOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_CREATE_TASK_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_CREATE_TASK_OPTION);
            String[] tagArguments = new String[0];

            if (cmd.hasOption(CliConstants.CLI_INCLUDE_TASK_TAGS_OPTION)) {
                tagArguments = cmd.getOptionValues(CliConstants.CLI_INCLUDE_TASK_TAGS_OPTION);
            }

            Task task = new Task();
            task.setName(arguments[0]);
            task.setDescription(arguments[1]);
            task.setStatus(WorkStatus.valueOf(arguments[2].toUpperCase()));
            task.setTags(new ArrayList<>(List.of(tagArguments)));
            task.setComment(arguments[3].equals("null") ? null : arguments[3]);
            task.setProjectId(UUID.fromString(arguments[4]));
            task.setCompletedAt(arguments[5].equals("null") ? null : LocalDateTime.parse(arguments[5], dateTimeFormatter));
            task.setDeadline(LocalDateTime.parse(arguments[6], dateTimeFormatter));
            task.setPriority(Priority.valueOf(arguments[7].toUpperCase()));
            task.setEmployeeId(UUID.fromString(arguments[8]));
            task.setEmployeeFullName(arguments[9]);

            Result<NoData> result = provider.processNewTask(task);
            logger.info("createTaskOption[1]: статус создания задачи {}", result);
        }
    }

    private static void bindEmployeeToProjectOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_BIND_EMPLOYEE_TO_PROJECT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_BIND_EMPLOYEE_TO_PROJECT_OPTION);

            Result<NoData> result = provider.bindEmployeeToProject(UUID.fromString(arguments[0]), UUID.fromString(arguments[1]));
            logger.info("Main[10]: статус привязки сотрудника к проекту {}", result);
        }
    }

    private static void deleteProjectOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DELETE_PROJECT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DELETE_PROJECT_OPTION);
            Result<NoData> result = provider.deleteProject(UUID.fromString(arguments[0]));
            logger.info("Main[11]: статус удаления проекта {}", result);
        }
    }

    private static void deleteEmployeeOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DELETE_EMPLOYEE_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DELETE_EMPLOYEE_OPTION);
            Result<NoData> result = provider.deleteEmployee(UUID.fromString(arguments[0]));
            logger.info("Main[12]: статус удаления сотрудника {}", result);
        }
    }

    private static void deleteEventOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DELETE_EVENT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DELETE_EVENT_OPTION);
            Result<NoData> result = provider.deleteEvent(UUID.fromString(arguments[0]));
            logger.info("Main[13]: статус удаления события {}", result);
        }
    }

    private static void deleteBugReportOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DELETE_BUG_REPORT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DELETE_BUG_REPORT_OPTION);
            Result<NoData> result = provider.deleteBugReport(UUID.fromString(arguments[0]));
            logger.info("Main[14]: статус удаления баг репорта проекта {}", result);
        }
    }

    private static void deleteDocumentationOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DELETE_DOCUMENTATION_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DELETE_DOCUMENTATION_OPTION);
            Result<NoData> result = provider.deleteDocumentation(UUID.fromString(arguments[0]));
            logger.info("Main[15]: статус удаления документации проекта {}", result);
        }
    }

    private static void deleteTaskOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_DELETE_TASK_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_DELETE_TASK_OPTION);
            Result<NoData> result = provider.deleteTask(UUID.fromString(arguments[0]));
            logger.info("Main[16]: статус удаления задачи {}", result);
        }
    }

    private static void getProjectOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_PROJECT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_PROJECT_OPTION);
            Result<Project> result = provider.getProjectById(UUID.fromString(arguments[0]));
            printResultData(result.getData());
        }
    }

    private static void getTaskOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_TASK_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_TASK_OPTION);
            Result<Task> result = provider.getTaskById(UUID.fromString(arguments[0]));
            printResultData(result.getData());
        }
    }

    private static void getTasksByProjectIdOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_TASKS_BY_PROJECT_ID_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_TASKS_BY_PROJECT_ID_OPTION);

            Result<List<Task>> result = provider.getTasksByProjectId(UUID.fromString(arguments[0]));
            logger.info("getTasksByProjectId[1]: статус выполнения {}", result.getCode());
            result.getData().forEach(Main::printResultData);
        }
    }

    private static void getTasksByEmployeeIdOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION);

            Result<List<Task>> result = provider.getTasksByEmployeeId(UUID.fromString(arguments[0]));
            logger.info("getTasksByEmployeeId[1]: статус выполнения {}", result.getCode());
            result.getData().forEach(Main::printResultData);
        }
    }

    private static void getTasksByTagsOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_TASKS_BY_TAGS_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_TASKS_BY_TAGS_OPTION);

            UUID projectId = UUID.fromString(arguments[0]);
            ArrayList<String> tags = Arrays.stream(arguments, 1, arguments.length)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            logger.info(Arrays.toString(arguments));

            Result<List<Task>> result = provider.getTasksByTags(tags, projectId);
            logger.info("getTasksByTagsOption[1]: статус выполнения {}", result.getCode());
            if (!result.getData().isEmpty())
                result.getData().forEach(Main::printResultData);
        }
    }

    private static void getEventOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_EVENT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_EVENT_OPTION);

            Result<Event> result = provider.getEventById(UUID.fromString(arguments[0]));
            logger.info("getEventOption[1]: статус выполнения {}", result.getCode());
            printResultData(result.getData());
        }
    }

    private static void getEventsByProjectIdOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_EVENTS_BY_PROJECT_ID_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_EVENTS_BY_PROJECT_ID_OPTION);

            Result<List<Event>> result = provider.getEventsByProjectId(UUID.fromString(arguments[0]));
            logger.info("getEventsByProjectIdOption[1]: статус выполнения {}", result.getCode());
            result.getData().forEach(Main::printResultData);
        }
    }

    private static void getBugReportOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_BUG_REPORT_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_BUG_REPORT_OPTION);

            Result<BugReport> result = provider.getBugReportById(UUID.fromString(arguments[0]));
            logger.info("getBugReportOption[1]: статус выполнения {}", result.getCode());
            printResultData(result.getData());
        }
    }

    private static void getBugReportsByProjectIdOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION);

            Result<List<BugReport>> result = provider.getBugReportsByProjectId(UUID.fromString(arguments[0]));
            logger.info("getBugReportsByProjectIdOption[1]: статус выполнения {}", result.getCode());
            result.getData().forEach(Main::printResultData);
        }
    }

    private static void getDocumentationOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_DOCUMENTATION_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_DOCUMENTATION_OPTION);

            Result<Documentation> result = provider.getDocumentationById(UUID.fromString(arguments[0]));
            logger.info("getDocumentationOption[1]: статус выполнения {}", result.getCode());
            printResultData(result.getData());
        }
    }

    private static void getDocumentationsByProjectIdOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION);

            Result<List<Documentation>> result = provider.getDocumentationsByProjectId(UUID.fromString(arguments[0]));
            logger.info("getDocumentationsByProjectIdOption[1]: статус выполнения {}", result.getCode());
            result.getData().forEach(Main::printResultData);
        }
    }

    private static void getEmployeeOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_EMPLOYEE_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_EMPLOYEE_OPTION);

            Result<Employee> result = provider.getEmployeeById(UUID.fromString(arguments[0]));
            logger.info("getEmployeeOption[1]: статус выполнения {}", result.getCode());
            printResultData(result.getData());
        }
    }

    private static void getProjectTeamOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_GET_PROJECT_TEAM_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_GET_PROJECT_TEAM_OPTION);

            Result<List<Employee>> result = provider.getProjectTeam(UUID.fromString(arguments[0]));
            logger.info("getProjectTeamOption[1]: статус выполнения {}", result.getCode());
            result.getData().forEach(Main::printResultData);
        }
    }

    private static void completeTaskOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_COMPLETE_TASK_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_COMPLETE_TASK_OPTION);
            Result<NoData> result = provider.completeTask(UUID.fromString(arguments[0]));
            logger.info("completeTaskOption[1]: статус выполнения {}", result);
        }
    }

    private static void projectStatsOption(CommandLine cmd) {
        if (cmd.hasOption(CliConstants.CLI_PROJECT_STATS_OPTION)) {
            String[] arguments = cmd.getOptionValues(CliConstants.CLI_PROJECT_STATS_OPTION);
            boolean trackBugReports = false;
            boolean employeeEfficiency = false;

            if (cmd.hasOption(CliConstants.CLI_INCLUDE_BUG_REPORT_TRACKING))
                trackBugReports = true;
            if (cmd.hasOption(CliConstants.CLI_INCLUDE_EMPLOYEE_EFFICIENCY_OPTION))
                employeeEfficiency = true;

            ProjectStatistics result = provider.monitorProjectCharacteristics(
                    UUID.fromString(arguments[0]),
                    employeeEfficiency,
                    trackBugReports
            );

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String prettyOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

                logger.info("projectStatsOption[1]: {}", prettyOutput);
            }
            catch (JsonProcessingException e) {
                logger.error("projectStatsOption[2]: {}", e.getMessage());
            }
        }
    }
}