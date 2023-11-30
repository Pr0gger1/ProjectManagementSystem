package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.BugStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class BugReport extends ProjectEntity {
    private Priority priority = Priority.LOW;
    private BugStatus status = BugStatus.OPENED;


    public BugReport(
            String name,
            String description,
            UUID authorId,
            String authorFullName,
            String projectId
    ) {
        super(name, description, authorId, authorFullName, projectId);
    }

    public BugReport(
            String name,
            String description,
            UUID authorId,
            String authorFullName,
            String projectId,
            Priority priority
    ) {
        super(name, description, authorId, authorFullName, projectId);
        this.priority = priority;
    }


    // full constructor
    public BugReport(
            String name,
            String description,
            UUID id,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            Date createdAt,
            Priority priority,
            BugStatus status
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt);
        this.priority = priority;
        this.status = status;
    }

    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BugReport bugReport = (BugReport) object;
        return priority == bugReport.priority && status == bugReport.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, status);
    }

    @Override
    public String toString() {
        return "BugReport{" +
                "priority=" + priority +
                ", status=" + status +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", projectId='" + projectId + '\'' +
                ", employeeId=" + employeeId +
                ", employeeFullName='" + employeeFullName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
