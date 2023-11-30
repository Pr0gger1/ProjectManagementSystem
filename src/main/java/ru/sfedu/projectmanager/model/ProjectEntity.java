package ru.sfedu.projectmanager.model;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public abstract class ProjectEntity {
    protected UUID id;
    protected String name;
    protected String description;
    protected String projectId;
    protected UUID employeeId;
    protected String employeeFullName;
    protected Date createdAt;

    ProjectEntity(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId
    ) {
        createdAt = new Date();
        this.id = UUID.randomUUID();
        this.name = name;
        this.projectId = projectId;
        this.description = description;
        this.employeeId = employeeId;
        this.employeeFullName = employeeFullName;
    }


    ProjectEntity(
            String name,
            String description,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            Date createdAt
    ) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.employeeFullName = employeeFullName;
        this.createdAt = createdAt;
    }

    // full constructor
    ProjectEntity(
            String name,
            String description,
            UUID id,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            Date createdAt
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

    public void setProjectId(UUID id) {
        this.id = id;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public UUID getEmployeeId() {
        return employeeId;
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
