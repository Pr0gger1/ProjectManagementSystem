package ru.sfedu.projectmanagement.core.model;

import ru.sfedu.projectmanagement.core.utils.types.TrackInfo;

import java.util.Objects;

public class ProjectStatistics {
    private float projectReadiness;
    private TrackInfo<Task, String> taskStatus;
    private TrackInfo<Employee, Float> laborEfficiency;
    private TrackInfo<BugReport, String> bugReportStatus;

    public ProjectStatistics() {}


    public float getProjectReadiness() {
        return projectReadiness;
    }

    public void setProjectReadiness(float projectReadiness) {
        this.projectReadiness = projectReadiness;
    }

    public TrackInfo<Task, String> getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TrackInfo<Task, String> taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TrackInfo<Employee, Float> getLaborEfficiency() {
        return laborEfficiency;
    }

    public void setLaborEfficiency(TrackInfo<Employee, Float> laborEfficiency) {
        this.laborEfficiency = laborEfficiency;
    }

    public TrackInfo<BugReport, String> getBugReportStatus() {
        return bugReportStatus;
    }

    public void setBugReportStatus(TrackInfo<BugReport, String> bugReportStatus) {
        this.bugReportStatus = bugReportStatus;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProjectStatistics that = (ProjectStatistics) object;
        return Float.compare(projectReadiness, that.projectReadiness) == 0 && Objects.equals(taskStatus, that.taskStatus) && Objects.equals(laborEfficiency, that.laborEfficiency) && Objects.equals(bugReportStatus, that.bugReportStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectReadiness, taskStatus, laborEfficiency, bugReportStatus);
    }

    @Override
    public String toString() {
        return "ProjectStatistics{" +
                "projectReadiness=" + projectReadiness +
                ", taskStatus=" + taskStatus +
                ", laborEfficiency=" + laborEfficiency +
                ", bugReportStatus=" + bugReportStatus +
                '}';
    }
}
