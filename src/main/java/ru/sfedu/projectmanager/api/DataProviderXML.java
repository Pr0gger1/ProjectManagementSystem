package ru.sfedu.projectmanager.api;

import ru.sfedu.projectmanager.model.*;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.util.ArrayList;
import java.util.UUID;

public class DataProviderXML implements IDataProvider {

    /**
     * @param project
     * @return
     */
    @Override
    public Result<?> processNewProject(Project project) {
        return null;
    }

    /**
     * @param task
     * @return
     */
    @Override
    public Result<?> processNewTask(Task task) {
        return null;
    }

    /**
     * @param bugReport
     * @return
     */
    @Override
    public Result<?> processNewBugReport(BugReport bugReport) {
        return null;
    }

    /**
     * @param documentation
     * @return
     */
    @Override
    public Result<?> processNewDocumentation(Documentation documentation) {
        return null;
    }

    /**
     * @param event
     * @return
     */
    @Override
    public Result<?> processNewEvent(Event event) {
        return null;
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
     * @return
     */
    @Override
    public TrackInfo<Task, WorkStatus> trackTaskStatus() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<BugReport, WorkStatus> trackBugReportStatus() {
        return null;
    }


    /**
     * @param employeeId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindEmployeeToProject(UUID employeeId, String projectId) {
        return null;
    }

    /**
     * @param managerId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindProjectManager(UUID managerId, String projectId) {
        return null;
    }

    /**
     * @param executorId
     * @param taskId
     * @param projectId
     * @return
     */
    @Override
    public Result<?> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, String projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<?> deleteProject(String projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<?> deleteTask(UUID taskId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<?> deleteBugReport(UUID bugReportId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<?> deleteEvent(UUID eventId) {
        return null;
    }

    /**
     * @param docId
     * @return
     */
    @Override
    public Result<?> deleteDocumentation(UUID docId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<?> deleteEmployee(UUID employeeId) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Result<Project> getProjectById(String id) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<ProjectEntity>> getTasksByProjectId(String projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<ProjectEntity> getTaskById(UUID taskId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<ProjectEntity>> getBugReportsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<ProjectEntity> getBugReportById(UUID bugReportId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<ProjectEntity>> getEventsByProjectId(String projectId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<ProjectEntity> getEventById(UUID eventId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ProjectEntity> getDocumentationByProjectId(String projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(String projectId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<Employee> getEmployeeById(UUID employeeId) {
        return null;
    }
}
