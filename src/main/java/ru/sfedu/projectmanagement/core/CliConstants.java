package ru.sfedu.projectmanagement.core;

public class CliConstants {
    public static final String SYSTEM_VAR_LOG4J_CONFIG_PATH = "log4j.configurationFile";

    // set properties file option
    public static final String CLI_ENVIRONMENT_PROPERTIES = "config";
    public static final String CLI_ENVIRONMENT_PROPERTIES_ARGNAME = "filePath";
    public static final String CLI_ENVIRONMENT_PROPERTIES_DESCRIPTION = "Путь до файла с конфигурацией properties";

    // set data source type option
    public static final String CLI_DATASOURCE_TYPE_OPTION = "dsType";
    public static final String CLI_DATASOURCE_TYPE_OPTION_ARGNAME = "csv, xml, postgres";
    public static final String CLI_DATASOURCE_TYPE_OPTION_DESCRIPTION = "Задает тип источника данных (csv, xml, postgres). По умолчанию postgres";

    // create project option
    public static final String CLI_CREATE_PROJECT_OPTION = "cp";
    public static final String CLI_CREATE_PROJECT_OPTION_ARGNAME = "name description manager_id status deadline";
    public static final String CLI_CREATE_PROJECT_OPTION_DESCRIPTION = "создание нового проекта. Где status=<in_progress,completed,frozen> в любом регистре";

    // create employee option
    public static final String CLI_CREATE_EMPLOYEE_OPTION = "cee";
    public static final String CLI_CREATE_EMPLOYEE_OPTION_ARGNAME = "firstName lastName patronymic email birthday phoneNumber position";
    public static final String CLI_CREATE_EMPLOYEE_OPTION_DESCRIPTION = "создание сотрудника";

    // create event option
    public static final String CLI_CREATE_EVENT_OPTION = "ce";
    public static final String CLI_CREATE_EVENT_OPTION_ARGNAME = "name description projectId employeeId employeeFullName startDate endDate";
    public static final String CLI_CREATE_EVENT_OPTION_DESCRIPTION = "создание события проекта";

    // create task option
    public static final String CLI_CREATE_TASK_OPTION = "ct";
    public static final String CLI_CREATE_TASK_OPTION_ARGNAME = "name description status comment projectId completedAt deadline priority employeeId employeeFullName [--tags]";
    public static final String CLI_CREATE_TASK_DESCRIPTION = "создание задачи проекта. Где priority=<low,medium,high> в любом регистре";

    // create bug report option
    public static final String CLI_CREATE_BUG_REPORT_OPTION = "cbr";
    public static final String CLI_CREATE_BUG_REPORT_OPTION_ARGNAME = "name description status priority projectId employeeId employeeFullName";
    public static final String CLI_CREATE_BUG_REPORT_OPTION_DESCRIPTION = "создание баг репорта проекта. Где status=<opened,closed,in_progress>, priority=<low,medium,high> в любом регистре";

    // create documentation option
    public static final String CLI_CREATE_DOCUMENTATION_OPTION = "cd";
    public static final String CLI_CREATE_DOCUMENTATION_OPTION_ARGNAME = "name description projectId employeeId employeeFullName [-doc]";
    public static final String CLI_CREATE_DOCUMENTATION_OPTION_DESCRIPTION = "создание документации проекта";

    public static final String CLI_DOC_DATA_OPTION = "doc";
    public static final String CLI_DOC_DATA_OPTION_ARGNAME = "{'article_title','article'},{...},...";
    public static final String CLI_DOC_DATA_OPTION_DESCRIPTION = "задает содержимое документации перед ее непосредственным созданием";


    public static final String CLI_BIND_EMPLOYEE_TO_PROJECT_OPTION = "be";
    public static final String CLI_BIND_EMPLOYEE_TO_PROJECT_OPTION_DESCRIPTION = "привязка сотрудника к проекту";

    // delete project option
    public static final String CLI_DELETE_PROJECT_OPTION = "dp";
    public static final String CLI_DELETE_PROJECT_OPTION_ARGNAME = "projectId";
    public static final String CLI_DELETE_PROJECT_OPTION_DESCRIPTION = "удаление проекта по его id";

    // delete employee option
    public static final String CLI_DELETE_EMPLOYEE_OPTION = "dee";
    public static final String CLI_DELETE_EMPLOYEE_OPTION_ARGNAME = "employeeId";
    public static final String CLI_DELETE_EMPLOYEE_OPTION_DESCRIPTION = "удаление сотрудника по его id";

    // delete task option
    public static final String CLI_DELETE_TASK_OPTION = "dt";
    public static final String CLI_DELETE_TASK_OPTION_ARGNAME = "taskId";
    public static final String CLI_DELETE_TASK_OPTION_DESCRIPTION = "удаление задачи по её id";

    // delete bug report option
    public static final String CLI_DELETE_BUG_REPORT_OPTION = "dbr";
    public static final String CLI_DELETE_BUG_REPORT_OPTION_ARGNAME = "bugReportId";
    public static final String CLI_DELETE_BUG_REPORT_OPTION_DESCRIPTION = "удаление баг репорта по его id";

    // delete event option
    public static final String CLI_DELETE_EVENT_OPTION = "de";
    public static final String CLI_DELETE_EVENT_OPTION_ARGNAME = "eventId";
    public static final String CLI_DELETE_EVENT_OPTION_DESCRIPTION = "удаление события по его id";

    // delete documentation option
    public static final String CLI_DELETE_DOCUMENTATION_OPTION = "dd";
    public static final String CLI_DELETE_DOCUMENTATION_OPTION_ARGNAME = "docId";
    public static final String CLI_DELETE_DOCUMENTATION_OPTION_DESCRIPTION = "удаление документации проекта по его id";

    // get project option
    public static final String CLI_GET_PROJECT_OPTION = "gp";
    public static final String CLI_GET_PROJECT_OPTION_ARGNAME = "projectId";
    public static final String CLI_GET_PROJECT_OPTION_DESCRIPTION = "выборка проекта по его id";

    // get task options
    public static final String CLI_GET_TASK_OPTION = "gt";
    public static final String CLI_GET_TASK_OPTION_ARGNAME = "taskId";
    public static final String CLI_GET_TASK_OPTION_DESCRIPTION = "выборка задачи по её id";

    public static final String CLI_GET_TASKS_BY_PROJECT_ID_OPTION = "gtp";
    public static final String CLI_GET_TASKS_BY_PROJECT_ID_OPTION_ARGNAME = "projectId";
    public static final String CLI_GET_TASKS_BY_PROJECT_ID_OPTION_DESCRIPTION = "выборка списка задач по id проекта";

    public static final String CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION = "gte";
    public static final String CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION_ARGNAME = "employeeId";
    public static final String CLI_GET_TASKS_BY_EMPLOYEE_ID_OPTION_DESCRIPTION = "выборка списка задач по id сотрудника";

    public static final String CLI_GET_TASKS_BY_TAGS_OPTION = "gtt";
    public static final String CLI_GET_TASKS_BY_TAGS_OPTION_ARGNAME = "список,тегов";
    public static final String CLI_GET_TASKS_BY_TAGS_OPTION_DESCRIPTION = "выборка списка задач по списку тегов (одному тегу)";

    // get bug report options
    public static final String CLI_GET_BUG_REPORT_OPTION = "gbr";
    public static final String CLI_GET_BUG_REPORT_OPTION_ARGNAME = "bugReportId";
    public static final String CLI_GET_BUG_REPORT_OPTION_DESCRIPTION = "выборка баг репорта по его id";

    public static final String CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION = "gbrp";
    public static final String CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION_ARGNAME = "projectId";
    public static final String CLI_GET_BUG_REPORTS_BY_PROJECT_ID_OPTION_DESCRIPTION = "выборка списка баг репортов по id проекта";

    // get event options
    public static final String CLI_GET_EVENT_OPTION = "ge";
    public static final String CLI_GET_EVENT_OPTION_ARGNAME = "eventId";
    public static final String CLI_GET_EVENT_OPTION_DESCRIPTION = "выборка события проекта по его id";

    public static final String CLI_GET_EVENTS_BY_PROJECT_ID_OPTION = "gep";
    public static final String CLI_GET_EVENTS_BY_PROJECT_ID_OPTION_ARGNAME = "projectId";
    public static final String CLI_GET_EVENTS_BY_PROJECT_ID_OPTION_DESCRIPTION = "выборка списка событий по id проекта";

    // get documentation options
    public static final String CLI_GET_DOCUMENTATION_OPTION = "gd";
    public static final String CLI_GET_DOCUMENTATION_OPTION_ARGNAME = "docId";
    public static final String CLI_GET_DOCUMENTATION_OPTION_DESCRIPTION = "выборка документации проекта по id";

    public static final String CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION = "gdp";
    public static final String CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION_ARGNAME = "projectId";
    public static final String CLI_GET_DOCUMENTATIONS_BY_PROJECT_ID_OPTION_DESCRIPTION = "выборка списка документаций по id проекта";

    // get employee options
    public static final String CLI_GET_EMPLOYEE_OPTION = "gee";
    public static final String CLI_GET_EMPLOYEE_OPTION_ARGNAME = "employeeId";
    public static final String CLI_GET_EMPLOYEE_OPTION_DESCRIPTION = "выборка сотрудника по его id";

    public static final String CLI_GET_PROJECT_TEAM_OPTION = "gpt";
    public static final String CLI_GET_PROJECT_TEAM_OPTION_ARGNAME = "projectId";
    public static final String CLI_GET_PROJECT_TEAM_OPTION_DESCRIPTION = "получение списка сотрудников, прикрепленных к проекту, по id проекта";

    public static final String CLI_HELP_OPTION = "help";
    public static final String CLI_HELP_OPTION_DESCRIPTION = "Справочная информация API";

    public static final String CLI_PROJECT_STATS_OPTION = "stats";
    public static final String CLI_PROJECT_STATS_OPTION_ARGNAME = "projectId";
    public static final String CLI_PROJECT_STATS_OPTION_DESCRIPTION = "Выводит статистику по проекту. Включает в себя мониторинг задач, баг репортов, сводку по эффективности труда сотрудников, степень готовности проекта в процентах";

    public static final String CLI_INCLUDE_BUG_REPORT_TRACKING = "brtrack";
    public static final String CLI_INCLUDE_BUG_REPORT_TRACKING_DESCRIPTION = "Выводит сводку по баг репортам проекта";
    public static final String CLI_INCLUDE_EMPLOYEE_EFFICIENCY_OPTION = "empeff";
    public static final String CLI_INCLUDE_EMPLOYEE_EFFICIENCY_OPTION_DESCRIPTION = "Выводит сводку эффективности труда сотрудников";

    public static final String CLI_INCLUDE_TASK_TAGS_OPTION = "tags";
    public static final String CLI_INCLUDE_TASK_TAGS_OPTION_ARGNAME = "tag1 tag2 ...";
    public static final String CLI_INCLUDE_TASK_TAGS_OPTION_DESCRIPTION = "Задает теги при создании задачи. Используется вместе с командой -ct";
}
