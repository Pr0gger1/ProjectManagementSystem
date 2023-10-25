package ru.sfedu.model;

import java.util.Date;

public class Task extends ProjectEntity {
    private Date deadline;
    private int executorId;
    private String comment;
    private Priority priority;
    private EntityStatus status = EntityStatus.IN_PROGRESS;

    Task(String name, String description) {
        super(name, description);
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

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }
}
