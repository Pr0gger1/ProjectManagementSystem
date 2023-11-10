package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.BugStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

public class BugReport extends ProjectEntity {
    private Priority priority;
    private int authorId;
    private String authorFullName;
    private BugStatus status = BugStatus.OPENED;

    BugReport(String name, String description, int authorId, String projectId) {
        super(name, description, projectId);
        this.authorId = authorId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getAuthor() {
        return authorId;
    }

    public void setAuthor(int authorId) {
        this.authorId = authorId;
    }

    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }
}
