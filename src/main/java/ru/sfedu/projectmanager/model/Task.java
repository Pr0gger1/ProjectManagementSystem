package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.WorkStatus;
import ru.sfedu.projectmanager.model.enums.Priority;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Task extends ProjectEntity {
    private LocalDateTime deadline;
    private String comment;
    private Priority priority = Priority.LOW;
    // TODO: преобразовать поле tag в список строк
    private String tag;
    private WorkStatus status = WorkStatus.IN_PROGRESS;
    private LocalDateTime completedAt = null;

    public Task(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId,
            LocalDateTime deadline,
            String comment,
            Priority priority,
            String tag,
            WorkStatus status,
            LocalDateTime createdAt
    ) {
        super(name, description, projectId, employeeId, employeeFullName, createdAt);
        this.deadline = deadline;
        this.comment = comment;
        this.priority = priority;
        this.tag = tag;
        this.status = status;
    }

    // full constructor
    public Task(
            String name,
            String description,
            UUID id,
            UUID employeeId,
            String employeeFullName,
            String projectId,
            LocalDateTime deadline,
            String comment,
            Priority priority,
            String tag,
            WorkStatus status,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt);
        this.deadline = deadline;
        this.comment = comment;
        this.priority = priority;
        this.tag = tag;
        this.status = status;
        this.completedAt = completedAt;
    }

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
            String projectId
    ) {
        super(name, description, null, null, projectId);
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

    public void completeTask() {
        this.completedAt = LocalDateTime.now().withNano(0);
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Task task = (Task) object;
        return Objects.equals(deadline, task.deadline) && Objects.equals(comment, task.comment) && priority == task.priority && Objects.equals(tag, task.tag) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deadline, comment, priority, tag, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "deadline=" + deadline +
                ",comment='" + comment + '\'' +
                ",priority=" + priority +
                ",tag='" + tag + '\'' +
                ",status=" + status +
                ",id=" + id +
                ",name='" + name + '\'' +
                ",description='" + description + '\'' +
                ",projectId='" + projectId + '\'' +
                ",employeeId=" + employeeId +
                ",employeeFullName='" + employeeFullName + '\'' +
                ",createdAt=" + createdAt +
                '}';
    }
}
