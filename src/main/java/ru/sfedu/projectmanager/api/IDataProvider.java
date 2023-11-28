package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.util.ArrayList;
import java.util.UUID;

public interface IDataProvider {
    boolean processNewProject(Project project);
    boolean processNewTask(Task task);
    boolean processNewBugReport(BugReport bugReport);
    boolean processNewDocumentation(Documentation documentation);
    boolean processNewEvent(Event event);

    TrackInfo<String, TrackInfo<Object, Object>> monitorProjectReadiness(
        String projectId, boolean checkLaborEfficiency
    );
    int calculateProjectReadiness(String projectOd);
    TrackInfo<Employee, Integer> calculateLaborEfficiency(String projectId);
    TrackInfo<Task, WorkStatus> trackTaskStatus();
    TrackInfo<BugReport, WorkStatus> trackBugReportStatus();

    void bindEntityToProject(ProjectEntity entity, String projectId);
    void bindProjectManager(Employee manager, String projectId);
    void bindTaskExecutor(Employee executor, String projectId);

    boolean deleteProject(String projectId);
    boolean deleteTask(UUID taskId);
    boolean deleteBugReport(UUID bugReportId);
    boolean deleteEvent(UUID eventId);
    boolean deleteDocumentation(UUID docId);
    boolean deleteEmployee(UUID employeeId);


    Project getProjectById(String id);
    ArrayList<Task> getTaskByProjectId(String projectId);
    Task getTaskById(UUID taskId);
    ArrayList<BugReport> getBugReportsByProjectId(String projectId);
    BugReport getBugReportById(UUID bugReportId);
    ArrayList<Event> getEventsByProjectId(String projectId);
    Event getEventById(UUID eventId);
    Documentation getDocumentationByProjectId(String projectId);
    ArrayList<Employee> getProjectTeam(String projectId);
    Employee getEmployee(UUID employeeId);
    ProjectEntity getProjectEntityById(int id);
}
