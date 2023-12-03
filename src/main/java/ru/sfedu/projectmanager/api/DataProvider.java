package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;

import java.util.ArrayList;
import java.util.UUID;

public abstract class DataProvider {
    public abstract Result<?> processNewProject(Project project);
    public abstract Result<?> processNewTask(Task task);
    public abstract Result<?> processNewBugReport(BugReport bugReport);
    public abstract Result<?> processNewDocumentation(Documentation documentation);
    public abstract Result<?> processNewEvent(Event event);

    public abstract TrackInfo<String, ?> monitorProjectCharacteristics(
            String projectId, boolean checkLaborEfficiency, boolean trackBugs
    );
    protected abstract float calculateProjectReadiness(String projectId);
    protected abstract TrackInfo<Employee, Float> calculateLaborEfficiency(String projectId);
    protected abstract TrackInfo<Task, String> trackTaskStatus(String projectId);
    protected abstract TrackInfo<BugReport, String> trackBugReportStatus(String projectId);

    public abstract Result<?> bindEmployeeToProject(UUID employeeId, String projectId);
    public abstract Result<?> bindProjectManager(UUID managerId, String projectId);
    public abstract Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, String projectId);

    public abstract Result<?> deleteProject(String projectId);
    public abstract Result<?> deleteTask(UUID taskId);
    public abstract Result<?> deleteBugReport(UUID bugReportId);
    public abstract Result<?> deleteEvent(UUID eventId);
    public abstract Result<?> deleteDocumentation(UUID docId);
    public abstract Result<?> deleteEmployee(UUID employeeId);


    public abstract Result<Project> getProjectById(String id);
    public abstract Result<ArrayList<Task>> getTasksByProjectId(String projectId);
    public abstract Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId);
    public abstract Result<Task> getTaskById(UUID taskId);
    public abstract Result<ArrayList<BugReport>> getBugReportsByProjectId(String projectId);
    public abstract Result<BugReport> getBugReportById(UUID bugReportId);
    public abstract Result<ArrayList<Event>> getEventsByProjectId(String projectId);
    public abstract Result<Event> getEventById(UUID eventId);
    public abstract Result<ArrayList<Documentation>> getDocumentationsByProjectId(String projectId);
    public abstract Result<Documentation> getDocumentationById(UUID docId);
    public abstract Result<ArrayList<Employee>> getProjectTeam(String projectId);
    public abstract Result<Employee> getEmployeeById(UUID employeeId);
}
