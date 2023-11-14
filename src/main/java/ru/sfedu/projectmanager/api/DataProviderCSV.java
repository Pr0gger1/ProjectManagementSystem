package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

public class DataProviderCSV implements IDataProvider {

    /**
     * @param project
     */
    @Override
    public void processNewProject(Project project) {

    }

    /**
     * @param projectId
     * @param checkLaborEfficiency
     * @return
     */
    @Override
    public TrackInfo<String, TrackInfo<Object, Object>> monitorProjectReadiness(
            String projectId, boolean checkLaborEfficiency
    ) {
        return null;
    }

    /**
     * @param projectOd
     * @return
     */
    @Override
    public int calculateProjectReadiness(String projectOd) {
        return 0;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public TrackInfo<Employee, Integer> calculateLaborEfficiency(String projectId) {
        return null;
    }

    /**
     * @param task
     */
    @Override
    public void processNewTask(Task task) {

    }

    /**
     * @return
     */
    @Override
    public TrackInfo<Task, WorkStatus> trackTaskStatus() {
        return null;
    }

    /**
     * @param bugReport
     */
    @Override
    public void processNewBugReport(BugReport bugReport) {

    }

    /**
     * @return
     */
    @Override
    public TrackInfo<BugReport, WorkStatus> trackBugReportStatus() {
        return null;
    }

    /**
     * @param documentation
     */
    @Override
    public void processNewDocumentation(Documentation documentation) {

    }

    /**
     * @param event
     */
    @Override
    public void processNewEvent(Event event) {

    }

    /**
     * @return
     */
    @Override
    public String generateUUID() {
        return null;
    }

    /**
     * @param entity
     */
    @Override
    public void deleteProjectEntity(ProjectEntity entity) {

    }

    /**
     * @param project
     */
    @Override
    public void deleteProject(Project project) {

    }

    /**
     * @param manager
     * @param projectId
     */
    @Override
    public void bindProjectManager(Employee manager, String projectId) {

    }

    /**
     * @param executor
     * @param projectId
     */
    @Override
    public void bindTaskExecutor(Employee executor, String projectId) {

    }

    /**
     * @param id
     * @return
     */
    @Override
    public Project getProjectById(String id) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public ProjectEntity getProjectEntityById(int id) {
        return null;
    }

    /**
     * @param entity
     * @param projectId
     */
    @Override
    public void bindEntityToProject(ProjectEntity entity, String projectId) {

    }
}
