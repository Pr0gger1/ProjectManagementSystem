package ru.sfedu.projectmanagement.core.model;

import jakarta.xml.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectEntity")
public abstract class ProjectEntity {
    @XmlAttribute(required = true)
    protected UUID id;

    @XmlElement(required = true)
    protected String name;

    @XmlElement(nillable = true)
    protected String description;

    @XmlElement(required = true)
    protected String projectId;

    @XmlElement(required = true)
    protected UUID employeeId;

    @XmlElement(required = true)
    protected String employeeFullName;

    @XmlElement(required = true)
    protected LocalDateTime createdAt;

    ProjectEntity() {
        id = UUID.randomUUID();
    }

    ProjectEntity(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId
    ) {
        createdAt = LocalDateTime.now();
        this.id = UUID.randomUUID();
        this.name = name;
        this.projectId = projectId;
        this.description = description;
        this.employeeId = employeeId;
        this.employeeFullName = employeeFullName;
    }


    // full constructor
    ProjectEntity(
            String name,
            String description,
            UUID id,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.employeeFullName = employeeFullName;
        this.createdAt = createdAt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setProjectId(String id) {
        this.projectId = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProjectEntity that = (ProjectEntity) object;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(projectId, that.projectId) && Objects.equals(employeeId, that.employeeId) && Objects.equals(employeeFullName, that.employeeFullName) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, projectId, employeeId, employeeFullName, createdAt);
    }
}
