package ru.sfedu.projectmanagement.core.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.xml.adapters.XmlLocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task")
@XmlType(name = "Task")
public class Task extends ProjectEntity {
    @CsvBindByName(column = "deadline")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm")
    @XmlElement(name = "deadline")
    @XmlJavaTypeAdapter(XmlLocalDateTimeAdapter.class)
    private LocalDateTime deadline;

    @CsvBindByName(column = "comment")
    @XmlElement(name = "comment")
    private String comment;

    @CsvBindByName(column = "priority", required = true)
    @XmlElement(name = "priority", required = true)
    private Priority priority = Priority.LOW;

    @CsvIgnore
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    private List<String> tags = new ArrayList<>();

    @CsvBindByName(column = "status", required = true)
    @XmlElement(name = "status", required = true)
    private WorkStatus status = WorkStatus.IN_PROGRESS;

    @CsvBindByName(column = "completed_at")
    @CsvDate(value = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "completed_at")
    @XmlJavaTypeAdapter(XmlLocalDateTimeAdapter.class)
    private LocalDateTime completedAt = null;


    public Task() {
        super(EntityType.Task);
        createdAt = LocalDateTime.now().withNano(0);
    }

    public Task(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            UUID projectId,
            LocalDateTime deadline,
            String comment,
            Priority priority,
            ArrayList<String> tags,
            WorkStatus status,
            LocalDateTime createdAt
    ) {
        super(name, description, UUID.randomUUID(), projectId, employeeId, employeeFullName, createdAt, EntityType.Task);
        this.deadline = deadline;
        this.comment = comment;
        this.priority = priority;
        this.tags = tags;
        this.status = status;
    }


    // full constructor
    public Task(
            String name,
            String description,
            UUID id,
            UUID employeeId,
            String employeeFullName,
            UUID projectId,
            LocalDateTime deadline,
            String comment,
            Priority priority,
            ArrayList<String> tags,
            WorkStatus status,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt, EntityType.Task);
        this.deadline = deadline;
        this.comment = comment;
        this.priority = priority;
        this.tags = tags;
        this.status = status;
        this.completedAt = completedAt;
    }


    public Task(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            UUID projectId
    ) {
        super(name, description, employeeId, employeeFullName, projectId, EntityType.Task);
    }

    public Task(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            UUID projectId,
            Priority priority
    ) {
        super(name, description, employeeId, employeeFullName, projectId, EntityType.Task);
        this.priority = priority;
    }

    public void completeTask() {
        this.status = WorkStatus.COMPLETED;
        this.completedAt = LocalDateTime.now().withNano(0);
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        if (completedAt != null)
            this.status = WorkStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public void addTag(String tag) {
        tags.add(tag);
    }

    public List<String> getTags() {
        return tags;
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
        return Objects.equals(deadline, task.deadline) && Objects.equals(comment, task.comment) && priority == task.priority && Objects.equals(tags, task.tags) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deadline, comment, priority, tags, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "deadline=" + deadline +
                ",comment='" + comment + '\'' +
                ",priority=" + priority +
                ",tag='" + tags + '\'' +
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
