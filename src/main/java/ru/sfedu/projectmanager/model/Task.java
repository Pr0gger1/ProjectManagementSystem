package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.WorkStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

import java.util.Calendar;
import java.util.UUID;

public class Task extends ProjectEntity {
    private Calendar deadline;
    private String comment;
    private Priority priority = Priority.LOW;
    private String tag;
    private WorkStatus status = WorkStatus.IN_PROGRESS;

    public Task(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
    }

    public Task(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId,
            Priority priority
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
        this.priority = priority;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }
}
