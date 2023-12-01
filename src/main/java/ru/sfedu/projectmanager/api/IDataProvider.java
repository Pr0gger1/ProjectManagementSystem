package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.util.ArrayList;
import java.util.UUID;

public interface IDataProvider {
    Result<?> processNewProject(Project project);
    Result<?> processNewTask(Task task);
    Result<?> processNewBugReport(BugReport bugReport);
    Result<?> processNewDocumentation(Documentation documentation);
    Result<?> processNewEvent(Event event);

    TrackInfo<String, ?> monitorProjectReadiness(
        String projectId, boolean checkLaborEfficiency, boolean trackBugs
    );
    float calculateProjectReadiness(String projectId);
    TrackInfo<Employee, Float> calculateLaborEfficiency(String projectId);
    TrackInfo<Task, String> trackTaskStatus(String projectId);
    TrackInfo<BugReport, String> trackBugReportStatus(String projectId);

    Result<?> bindEmployeeToProject(UUID employeeId, String projectId);
    Result<?> bindProjectManager(UUID managerId, String projectId);
    Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, String projectId);

    Result<?> deleteProject(String projectId);
    Result<?> deleteTask(UUID taskId);
    Result<?> deleteBugReport(UUID bugReportId);
    Result<?> deleteEvent(UUID eventId);
    Result<?> deleteDocumentation(UUID docId);
    Result<?> deleteEmployee(UUID employeeId);


    Result<Project> getProjectById(String id);
    Result<ArrayList<Task>> getTasksByProjectId(String projectId);
    Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId);
    Result<Task> getTaskById(UUID taskId);
    Result<ArrayList<BugReport>> getBugReportsByProjectId(String projectId);
    Result<BugReport> getBugReportById(UUID bugReportId);
    Result<ArrayList<Event>> getEventsByProjectId(String projectId);
    Result<Event> getEventById(UUID eventId);
    Result<Documentation> getDocumentationByProjectId(String projectId);
    Result<ArrayList<Employee>> getProjectTeam(String projectId);
    Result<Employee> getEmployeeById(UUID employeeId);
}
