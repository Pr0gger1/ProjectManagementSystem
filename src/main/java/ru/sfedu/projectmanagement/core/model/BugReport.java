package ru.sfedu.projectmanagement.core.model;

import com.opencsv.bean.CsvBindByName;
import jakarta.xml.bind.annotation.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;
import ru.sfedu.projectmanagement.core.model.enums.Priority;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bug_report")
@XmlType(name = "BugReport")
public class BugReport extends ProjectEntity {
    @CsvBindByName(column = "priority", required = true)
    @XmlElement(required = true)
    private Priority priority = Priority.LOW;

    @CsvBindByName(column = "status", required = true)
    @XmlElement(required = true)
    private BugStatus status = BugStatus.OPENED;

    public BugReport() {
        super(EntityType.BugReport);
    }

    public BugReport(
            String name,
            String description,
            UUID authorId,
            String authorFullName,
            UUID projectId
    ) {
        super(name, description, authorId, authorFullName, projectId, EntityType.BugReport);
    }

    public BugReport(
            String name,
            String description,
            UUID authorId,
            String authorFullName,
            UUID projectId,
            Priority priority
    ) {
        super(name, description, authorId, authorFullName, projectId, EntityType.BugReport);
        this.priority = priority;
    }


    // full constructor
    public BugReport(
            String name,
            String description,
            UUID id,
            UUID projectId,
            UUID employeeId,
            String employeeFullName,
            LocalDateTime createdAt,
            Priority priority,
            BugStatus status
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt, EntityType.BugReport);
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
