package ru.sfedu.projectmanager.model;

import java.util.Date;
import java.util.UUID;

public abstract class ProjectEntity {
    protected UUID id = UUID.randomUUID();
    protected String name;
    protected String description;
    protected String projectId;
    protected UUID employeeId;
    protected String employeeFullName;
    protected Date createdAt;

    ProjectEntity(String name, String description, UUID employeeId, String employeeFullName, String projectId) {
        createdAt = new Date();
        this.name = name;
        this.projectId = projectId;
        this.description = description;
        this.employeeId = employeeId;
        this.employeeFullName = employeeFullName;
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
}
