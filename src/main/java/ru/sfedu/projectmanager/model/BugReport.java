package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.BugStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

public class BugReport extends ProjectEntity {
    private Priority priority;
    private BugStatus status = BugStatus.OPENED;

    BugReport(String name, String description, int authorId, String authorFullName, String projectId) {
        super(name, description, authorId, authorFullName, projectId);
    }


    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }
}
