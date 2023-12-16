package ru.sfedu.projectmanagement.core.api;

import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;

import java.util.ArrayList;
import java.util.UUID;

public class CsvDataProvider extends DataProvider {

    /**
     * @param project
     * @return
     */
    @Override
    public Result<NoData> processNewProject(Project project) {
        return null;
    }

    /**
     * @param task
     * @return
     */
    @Override
    public Result<NoData> processNewTask(Task task) {
        return null;
    }

    /**
     * @param bugReport
     * @return
     */
    @Override
    public Result<NoData> processNewBugReport(BugReport bugReport) {
        return null;
    }

    /**
     * @param documentation
     * @return
     */
    @Override
    public Result<NoData> processNewDocumentation(Documentation documentation) {
        return null;
    }

    /**
     * @param event
     * @return
     */
    @Override
    public Result<NoData> processNewEvent(Event event) {
        return null;
    }

    /**
     * @param employee
     * @return
     */
    @Override
    public Result<NoData> processNewEmployee(Employee employee) {
        return null;
    }

    /**
     * @param projectId
     * @param checkLaborEfficiency
     * @return
     */
    @Override
    public ProjectStatistics monitorProjectCharacteristics(
            UUID projectId, boolean checkLaborEfficiency, boolean trackBugs
    ) {
        return null;
    }

    /**
     * @param projectOd
     * @return
     */
    @Override
    public float calculateProjectReadiness(UUID projectOd) {
        return 0;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public TrackInfo<Employee, Float> calculateLaborEfficiency(UUID projectId) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<Task, String> trackTaskStatus(UUID projectId) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public TrackInfo<BugReport, String> trackBugReportStatus(UUID projectId) {
        return null;
    }


    /**
     * @param employeeId
     * @param projectId
     * @return
     */
    @Override
    public Result<NoData> bindEmployeeToProject(UUID employeeId, UUID projectId) {
        return null;
    }

    /**
     * @param managerId
     * @param projectId
     * @return
     */
    @Override
    public Result<NoData> bindProjectManager(UUID managerId, UUID projectId) {
        return null;
    }

    /**
     * @param executorId
     * @param taskId
     * @param projectId
     * @return
     */
    @Override
    public Result<NoData> bindTaskExecutor(UUID executorId, String executorFullName, UUID taskId, UUID projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<NoData> deleteProject(UUID projectId) {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<NoData> deleteTask(UUID taskId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<NoData> deleteBugReport(UUID bugReportId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<NoData> deleteEvent(UUID eventId) {
        return null;
    }

    /**
     * @param docId
     * @return
     */
    @Override
    public Result<NoData> deleteDocumentation(UUID docId) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Result<NoData> deleteEmployee(UUID employeeId) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Result<Project> getProjectById(UUID id) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByProjectId(UUID projectId) {
        return null;
    }

    /**
     *
     * @param employeeId
     * @return
     */
    @Override
    public Result<ArrayList<Task>> getTasksByEmployeeId(UUID employeeId) { return null; }

    /**
     * @param taskId
     * @return
     */
    @Override
    public Result<Task> getTaskById(UUID taskId) {
        return null;
    }

    @Override
    public Result<ArrayList<Task>> getTasksByTags(ArrayList<String> tags, UUID projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<BugReport>> getBugReportsByProjectId(UUID projectId) {
        return null;
    }

    /**
     * @param bugReportId
     * @return
     */
    @Override
    public Result<BugReport> getBugReportById(UUID bugReportId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Event>> getEventsByProjectId(UUID projectId) {
        return null;
    }

    /**
     * @param eventId
     * @return
     */
    @Override
    public Result<Event> getEventById(UUID eventId) {
        return null;
    }

    /**
     * @param docId
     * @return
     */
    @Override
    public Result<Documentation> getDocumentationById(UUID docId) { return null; }

    @Override
    public Result<ArrayList<Documentation>> getDocumentationsByProjectId(UUID projectId) {
        return null;
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Result<ArrayList<Employee>> getProjectTeam(UUID projectId) {
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
