package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.util.ArrayList;
import java.util.UUID;

public class DataProviderXML implements IDataProvider {

    /**
     * @param project
     */
    @Override
    public boolean processNewProject(Project project) {
        return true;
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
     * @return
     */
    @Override
    public boolean processNewTask(Task task) {

        return false;
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
    public boolean processNewBugReport(BugReport bugReport) {
        return true;
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
    public boolean processNewDocumentation(Documentation documentation) {
        return true;
    }

    /**
     * @param event
     */
    @Override
    public boolean processNewEvent(Event event) {
        return true;
    }

    /**
     * @param projectId
     */
    @Override
    public boolean deleteProject(String projectId) {
        return true;
    }

    /**
     * @param taskId
     */
    @Override
    public boolean deleteTask(UUID taskId) {
        return true;
    }

    /**
     * @param bugReportId
     */
    @Override
    public boolean deleteBugReport(UUID bugReportId) {
        return true;
    }

    /**
     * @param eventId
     */
    @Override
    public boolean deleteEvent(UUID eventId) {
        return true;
    }

    /**
     * @param docId
     */
    @Override
    public boolean deleteDocumentation(UUID docId) {
        return true;
    }

    /**
     * @param employeeId
     */
    @Override
    public boolean deleteEmployee(UUID employeeId) {
        return true;
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
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<Task> getTaskByProjectId(String projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Task getTaskById(UUID taskId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<BugReport> getBugReportsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public BugReport getBugReportById(UUID bugReportId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<Event> getEventsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Event getEventById(UUID eventId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Documentation getDocumentationByProjectId(String projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ArrayList<Employee> getProjectTeam(String projectId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Employee getEmployee(UUID employeeId) {
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
