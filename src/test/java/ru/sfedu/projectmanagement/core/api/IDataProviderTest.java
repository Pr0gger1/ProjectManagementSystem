package ru.sfedu.projectmanagement.core.api;

public interface IDataProviderTest {
    void processNewProject();
    void createExistingProject();
    void processNewTask();
    void createExistingTasks();
    void processNewBugReport();
    void createExistingBugReports();
    void processNewDocumentation();
    void createExistingDocumentation();
    void processNewEvent();
    void createExistingEvent();
    void processNewEmployee();
    void createExistingEmployee();


    void monitorProjectCharacteristics();
    void initDataForMonitorProjectCharacteristics(DataProvider provider);
    void monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency();
    void monitorProjectCharacteristicsWithLaborEfficiency();
    void calculateProjectReadiness();
    void calculateProjectReadinessIfHasNoTasks();
    void calculateLaborEfficiency();
    void calculateLaborEfficiencyIfEmployeeHasNoTasks();
    void trackTaskStatus();
    void trackBugReportStatus();

    void bindEmployeeToProject();
    void bindProjectManager();
    void bindTaskExecutor();

    void deleteProject();
    void deleteNonExistentProject();
    void deleteTask();
    void deleteNonExistentTask();
    void deleteBugReport();
    void deleteNonExistentBugReport();
    void deleteEvent();
    void deleteNonExistentEvent();
    void deleteDocumentation();
    void deleteNonExistentDocumentation();
    void deleteEmployee();
    void deleteNonExistentEmployee();


    void getProjectById();
    void getNonExistentProject();
    void getTasksByTags();
    void getTasksByNonExistentTags();
    void getTasksByProjectId();
    void getTasksFromProjectWithNoTasks();
    void getTasksByNonExistentProjectId();
    void getTasksByEmployeeId();
    void getTasksByNonExistentEmployeeId();
    void getTaskById();
    void getNonExistentTask();
    void getBugReportsByProjectId();
    void getBugReportsByNonExistentProjectId();
    void getBugReportById();
    void getNonExistentBugReport();
    void getEventsByProjectId();
    void getEventsByNonExistentProjectId();
    void getEventById();
    void getNonExistentEvent();
    void getDocumentationsByProjectId();
    void getDocumentationsByNonExistentProjectId();
    void getDocumentationById();
    void getNonExistentDocumentation();
    void getProjectTeam();
    void getProjectTeamOfNonExistentProject();
    void getEmptyProjectTeam();
    void getEmployeeById();
    void getNonExistentEmployee();
}
