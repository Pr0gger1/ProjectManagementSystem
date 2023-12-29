package ru.sfedu.projectmanagement.core.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.sfedu.projectmanagement.core.CliConstants;
import ru.sfedu.projectmanagement.core.Constants;

public class CliUtils {
    public static Options getAllOptions() {
        Options options = new Options();

        Option propertiesFileOption = Option.builder()
                .desc(CliConstants.CLI_ENVIRONMENT_PROPERTIES_DESCRIPTION)
                .argName(CliConstants.CLI_ENVIRONMENT_PROPERTIES_ARGNAME)
                .longOpt(CliConstants.CLI_ENVIRONMENT_PROPERTIES)
                .hasArg()
                .build();

        Option dataSourceTypeOption = Option.builder()
                .desc(CliConstants.CLI_DATASOURCE_TYPE_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_DATASOURCE_TYPE_OPTION_ARGNAME)
                .longOpt(CliConstants.CLI_DATASOURCE_TYPE_OPTION)
                .hasArg()
                .build();

        Option createProjectOption = Option.builder(CliConstants.CLI_CREATE_PROJECT_OPTION)
                .desc(CliConstants.CLI_CREATE_PROJECT_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_CREATE_PROJECT_OPTION_ARGNAME)
                .numberOfArgs(Constants.PROJECT_PRIMITIVE_PARAMETER_COUNT)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option createEmployeeOption = Option.builder(CliConstants.CLI_CREATE_EMPLOYEE_OPTION)
                .desc(CliConstants.CLI_CREATE_EMPLOYEE_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_CREATE_EMPLOYEE_OPTION_ARGNAME)
                .numberOfArgs(Constants.EMPLOYEE_PRIMITIVE_PARAMETER_COUNT)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option createTaskOption = Option.builder(CliConstants.CLI_CREATE_TASK_OPTION)
                .desc(CliConstants.CLI_CREATE_TASK_DESCRIPTION)
                .argName(CliConstants.CLI_CREATE_TASK_OPTION_ARGNAME)
                .numberOfArgs(Constants.TASK_PRIMITIVE_PARAMETER_COUNT)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option includeTaskTagsOption = Option.builder()
                .longOpt(CliConstants.CLI_INCLUDE_TASK_TAGS_OPTION)
                .argName(CliConstants.CLI_INCLUDE_TASK_TAGS_OPTION_ARGNAME)
                .desc(CliConstants.CLI_INCLUDE_TASK_TAGS_OPTION_DESCRIPTION)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option createBugReportOption = Option.builder(CliConstants.CLI_CREATE_BUG_REPORT_OPTION)
                .desc(CliConstants.CLI_CREATE_BUG_REPORT_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_CREATE_BUG_REPORT_OPTION_ARGNAME)
                .numberOfArgs(Constants.BUG_REPORT_PRIMITIVE_PARAMETER_COUNT)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option createEventOption = Option.builder(CliConstants.CLI_CREATE_EVENT_OPTION)
                .desc(CliConstants.CLI_CREATE_EVENT_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_CREATE_EVENT_OPTION_ARGNAME)
                .numberOfArgs(Constants.EVENT_PRIMITIVE_PARAMETER_COUNT)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option createDocumentationOption = Option.builder(CliConstants.CLI_CREATE_DOCUMENTATION_OPTION)
                .desc(CliConstants.CLI_CREATE_DOCUMENTATION_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_CREATE_DOCUMENTATION_OPTION_ARGNAME)
                .numberOfArgs(Constants.DOCUMENTATION_PRIMITIVE_PARAMETER_COUNT)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option bindEmployeeToProject = Option.builder(CliConstants.CLI_BIND_EMPLOYEE_TO_PROJECT_OPTION)
                .desc(CliConstants.CLI_BIND_EMPLOYEE_TO_PROJECT_OPTION_DESCRIPTION)
                .numberOfArgs(2)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option parseDocumentBodyOption = Option.builder(CliConstants.CLI_DOC_DATA_OPTION)
                .desc(CliConstants.CLI_DOC_DATA_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_DOC_DATA_OPTION_ARGNAME)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option deleteProjectOption = Option.builder(CliConstants.CLI_DELETE_PROJECT_OPTION)
                .argName(CliConstants.CLI_DELETE_PROJECT_OPTION_ARGNAME)
                .desc(CliConstants.CLI_DELETE_PROJECT_OPTION_DESCRIPTION)
                .optionalArg(true)
                .hasArg()
                .build();

        Option deleteEmployeeOption = Option.builder(CliConstants.CLI_DELETE_EMPLOYEE_OPTION)
                .argName(CliConstants.CLI_DELETE_EMPLOYEE_OPTION_ARGNAME)
                .desc(CliConstants.CLI_DELETE_EMPLOYEE_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option deleteTaskOption = Option.builder(CliConstants.CLI_DELETE_TASK_OPTION)
                .argName(CliConstants.CLI_DELETE_TASK_OPTION_ARGNAME)
                .desc(CliConstants.CLI_DELETE_TASK_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option deleteBugReportOption = Option.builder(CliConstants.CLI_DELETE_BUG_REPORT_OPTION)
                .argName(CliConstants.CLI_DELETE_BUG_REPORT_OPTION_ARGNAME)
                .desc(CliConstants.CLI_DELETE_BUG_REPORT_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option deleteEventOption = Option.builder(CliConstants.CLI_DELETE_EVENT_OPTION)
                .argName(CliConstants.CLI_DELETE_EVENT_OPTION_ARGNAME)
                .desc(CliConstants.CLI_DELETE_EVENT_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option deleteDocumentationOption = Option.builder(CliConstants.CLI_DELETE_DOCUMENTATION_OPTION)
                .argName(CliConstants.CLI_DELETE_DOCUMENTATION_OPTION_ARGNAME)
                .desc(CliConstants.CLI_DELETE_DOCUMENTATION_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getProjectOption = Option.builder(CliConstants.CLI_GET_PROJECT_OPTION)
                .argName(CliConstants.CLI_GET_PROJECT_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_PROJECT_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        // Опции для операции "get" задачи
        Option getTaskOption = Option.builder(CliConstants.CLI_GET_TASK_OPTION)
                .argName(CliConstants.CLI_GET_TASK_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_TASK_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getTasksByProjectIdOption = Option.builder(CliConstants.CLI_GET_TASKS_BY_PROJECT_ID_OPTION)
                .argName(CliConstants.CLI_GET_TASKS_BY_PROJECT_ID_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_TASKS_BY_PROJECT_ID_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getTasksByEmployeeIdOption = Option.builder(CliConstants.CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION)
                .argName(CliConstants.CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION_DESCRIPTION)
                .optionalArg(true)
                .hasArg()
                .build();

        Option getTasksByTagsOption = Option.builder(CliConstants.CLI_GET_TASKS_BY_TAGS_OPTION)
                .argName(CliConstants.CLI_GET_TASKS_BY_TAGS_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_TASKS_BY_TAGS_OPTION_DESCRIPTION)
                .optionalArg(true)
                .hasArgs()
                .build();

        Option getBugReportOption = Option.builder(CliConstants.CLI_GET_BUG_REPORT_OPTION)
                .argName(CliConstants.CLI_GET_BUG_REPORT_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_BUG_REPORT_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getBugReportsByProjectIdOption = Option.builder(CliConstants.CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION)
                .argName(CliConstants.CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        // Опции для операции "get" события
        Option getEventOption = Option.builder(CliConstants.CLI_GET_EVENT_OPTION)
                .argName(CliConstants.CLI_GET_EVENT_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_EVENT_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getEventsByProjectIdOption = Option.builder(CliConstants.CLI_GET_EVENTS_BY_PROJECT_ID_OPTION)
                .argName(CliConstants.CLI_GET_EVENTS_BY_PROJECT_ID_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_EVENTS_BY_PROJECT_ID_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        // Опции для операции "get" документации
        Option getDocumentationOption = Option.builder(CliConstants.CLI_GET_DOCUMENTATION_OPTION)
                .argName(CliConstants.CLI_GET_DOCUMENTATION_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_DOCUMENTATION_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getDocumentationsByProjectIdOption = Option.builder(CliConstants.CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION)
                .argName(CliConstants.CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        // Опции для операции "get" сотрудника
        Option getEmployeeOption = Option.builder(CliConstants.CLI_GET_EMPLOYEE_OPTION)
                .argName(CliConstants.CLI_GET_EMPLOYEE_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_EMPLOYEE_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option getProjectTeamOption = Option.builder(CliConstants.CLI_GET_PROJECT_TEAM_OPTION)
                .argName(CliConstants.CLI_GET_PROJECT_TEAM_OPTION_ARGNAME)
                .desc(CliConstants.CLI_GET_PROJECT_TEAM_OPTION_DESCRIPTION)
                .hasArg()
                .optionalArg(true)
                .build();

        Option helpOption = Option.builder()
                .desc(CliConstants.CLI_HELP_OPTION_DESCRIPTION)
                .longOpt(CliConstants.CLI_HELP_OPTION)
                .hasArg(false)
                .optionalArg(true)
                .build();

        Option projectStatsOption = Option.builder()
                .longOpt(CliConstants.CLI_PROJECT_STATS_OPTION)
                .desc(CliConstants.CLI_PROJECT_STATS_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_PROJECT_STATS_OPTION_ARGNAME)
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .build();

        Option trackBugReportOption = Option.builder()
                .longOpt(CliConstants.CLI_INCLUDE_BUG_REPORT_TRACKING)
                .desc(CliConstants.CLI_INCLUDE_BUG_REPORT_TRACKING_DESCRIPTION)
                .hasArg(false)
                .optionalArg(true)
                .build();

        Option employeeEfficiencyOption = Option.builder()
                .longOpt(CliConstants.CLI_INCLUDE_EMPLOYEE_EFFICIENCY_OPTION)
                .desc(CliConstants.CLI_INCLUDE_EMPLOYEE_EFFICIENCY_OPTION_DESCRIPTION)
                .hasArg(false)
                .optionalArg(true)
                .build();

        Option completeTaskOption = Option.builder()
                .longOpt(CliConstants.CLI_COMPLETE_TASK_OPTION)
                .desc(CliConstants.CLI_COMPLETE_TASK_OPTION_DESCRIPTION)
                .argName(CliConstants.CLI_COMPLETE_TASK_OPTION_ARGNAME)
                .optionalArg(true)
                .hasArg()
                .build();


        options.addOption(dataSourceTypeOption)
                .addOption(getTasksByEmployeeIdOption)
                .addOption(getDocumentationsByProjectIdOption)
                .addOption(getBugReportsByProjectIdOption)
                .addOption(getEventsByProjectIdOption)
                .addOption(getTasksByProjectIdOption)
                .addOption(getBugReportOption)
                .addOption(getProjectOption)
                .addOption(getTaskOption)
                .addOption(getEventOption)
                .addOption(getEmployeeOption)
                .addOption(getProjectTeamOption)
                .addOption(getTasksByTagsOption)
                .addOption(getDocumentationOption)
                .addOption(parseDocumentBodyOption)
                .addOption(bindEmployeeToProject)
                .addOption(propertiesFileOption)
                .addOption(createDocumentationOption)
                .addOption(createBugReportOption)
                .addOption(createEmployeeOption)
                .addOption(createProjectOption)
                .addOption(createEventOption)
                .addOption(createTaskOption)
                .addOption(deleteDocumentationOption)
                .addOption(deleteBugReportOption)
                .addOption(deleteEmployeeOption)
                .addOption(deleteProjectOption)
                .addOption(deleteEventOption)
                .addOption(deleteTaskOption)
                .addOption(helpOption)
                .addOption(trackBugReportOption)
                .addOption(employeeEfficiencyOption)
                .addOption(projectStatsOption)
                .addOption(includeTaskTagsOption)
                .addOption(completeTaskOption);

        return options;
    }
}
