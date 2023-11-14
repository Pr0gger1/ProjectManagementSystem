package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

public interface IDataProvider {
    void processNewProject(Project project);
    TrackInfo<String, TrackInfo<Object, Object>> monitorProjectReadiness(
        String projectId, boolean checkLaborEfficiency
    );
    int calculateProjectReadiness(String projectOd);
    TrackInfo<Employee, Integer> calculateLaborEfficiency(String projectId);
    void bindEntityToProject(ProjectEntity entity, String projectId);
    void deleteProject(Project project);
    void bindProjectManager(Employee manager, String projectId);
    void bindTaskExecutor(Employee executor, String projectId);

    void processNewTask(Task task);
    TrackInfo<Task, WorkStatus> trackTaskStatus();

    void processNewBugReport(BugReport bugReport);
    TrackInfo<BugReport, WorkStatus> trackBugReportStatus();

    void processNewDocumentation(Documentation documentation);
    void processNewEvent(Event event);


    String generateUUID();

    void deleteProjectEntity(ProjectEntity entity);

    Project getProjectById(String id);
    ProjectEntity getProjectEntityById(int id);
}
