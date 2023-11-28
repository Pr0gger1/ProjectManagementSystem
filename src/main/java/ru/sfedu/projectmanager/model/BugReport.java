package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.BugStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

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
}
