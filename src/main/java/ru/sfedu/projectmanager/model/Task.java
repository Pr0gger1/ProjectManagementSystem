package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.WorkStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

import java.util.Date;

public class Task extends ProjectEntity {
    private Date deadline;
    private int executorId;
    private int executorFullName;
    private String comment;
    private Priority priority;
    private String tag;
    private WorkStatus status = WorkStatus.IN_PROGRESS;

    Task(String name, String description, String projectId) {
        super(name, description, projectId);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getExecutorId() {
        return this.executorId;
    }

    public void setExecutor(int executorId) {
        this.executorId = executorId;
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

    public void setExecutorFullName(int executorFullName) {
        this.executorFullName = executorFullName;
    }

    public int getExecutorFullName() {
        return executorFullName;
    }


}