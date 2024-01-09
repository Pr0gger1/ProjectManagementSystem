package ru.sfedu.projectmanagement.core.api;

public interface IDataProviderTest {
    void processNewProject();
    void processExistingProject();
    void processNewTasks();
    void processExistingTasks();
    void processNewBugReports();
    void processExistingBugReports();
    void processNewDocumentation();
    void processExistingDocumentation();
    void processNewEvent();
    void processExistingEvent();
    void processNewEmployee();
    void processExistingEmployee();


    void monitorProjectCharacteristics();
    void monitorNonExistentProjectCharacteristics();
    void initDataForMonitorProjectCharacteristics(IDataProvider provider);
    void monitorProjectCharacteristicsWithBugStatusAndLaborEfficiency();
    void monitorNonExistentProjectCharacteristicsWithLaborEfficiency();
    void monitorNonExistentProjectCharacteristicsWithBugStatusAndLaborEfficiency();
    void monitorProjectCharacteristicsWithLaborEfficiency();
    void calculateProjectReadiness();
    void calculateNonExistentProjectReadiness();
    void calculateProjectReadinessIfHasNoTasks();
    void calculateLaborEfficiency();
    void calculateLaborEfficiencyForNonExistentProject();
    void calculateLaborEfficiencyIfEmployeeHasNoTasks();
    void trackTaskStatus();
    void trackTaskStatusForNonExistentProject();
    void trackBugReportStatusForNonExistentProject();
    void trackBugReportStatus();

    void bindEmployeeToProject();
    void bindNonExistentEmployeeToProject();
    void bindNonExistentProjectManager();
    void bindEmployeeToMultipleProjects();
    void bindProjectManager();

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
    void getTasksFromNonExistentProject();
    void getTasksByEmployeeId();
    void getTasksByNonExistentEmployeeId();
    void getTaskById();
    void getNonExistentTask();
    void getBugReportsByProjectId();
    void getBugReportsFromProjectWithNoBugReports();
    void getBugReportsByNonExistentProjectId();
    void getBugReportById();
    void getNonExistentBugReport();
    void getEventsByProjectId();
    void getEventsByNonExistentProjectId();
    void getEventsFromProjectWithNoEvents();
    void getEventById();
    void getNonExistentEvent();
    void getDocumentationsByProjectId();
    void getDocumentationsByNonExistentProjectId();
    void getDocumentationsFromProjectWithNoDocumentations();
    void getDocumentationById();
    void getNonExistentDocumentation();
    void getProjectTeam();
    void getTeamOfNonExistentProject();
    void getEmptyProjectTeam();
    void getEmployeeById();
    void getNonExistentEmployee();
    void completeTask();
    void completeNonExistentTask();
}
