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

    TrackInfo<String, TrackInfo<Object, Object>> monitorProjectReadiness(
        String projectId, boolean checkLaborEfficiency
    );
    int calculateProjectReadiness(String projectOd);
    TrackInfo<Employee, Integer> calculateLaborEfficiency(String projectId);
    TrackInfo<Task, WorkStatus> trackTaskStatus();
    TrackInfo<BugReport, WorkStatus> trackBugReportStatus();

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
    Result<ArrayList<ProjectEntity>> getTasksByProjectId(String projectId);
    Result<ProjectEntity> getTaskById(UUID taskId);
    Result<ArrayList<ProjectEntity>> getBugReportsByProjectId(String projectId);
    Result<ProjectEntity> getBugReportById(UUID bugReportId);
    Result<ArrayList<ProjectEntity>> getEventsByProjectId(String projectId);
    Result<ProjectEntity> getEventById(UUID eventId);
    Result<ProjectEntity> getDocumentationByProjectId(String projectId);
    Result<ArrayList<Employee>> getProjectTeam(String projectId);
    Result<Employee> getEmployeeById(UUID employeeId);
}
