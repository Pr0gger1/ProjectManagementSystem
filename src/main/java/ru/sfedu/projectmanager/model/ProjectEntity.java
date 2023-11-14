package ru.sfedu.projectmanager.model;

import java.util.Date;

public abstract class ProjectEntity {
    protected String name;
    protected int id;
    protected String projectId;
    protected String description;
    protected int employeeId;
    protected String employeeFullName;
    protected Date createdAt;

    ProjectEntity(String name, String description, int employeeId, String employeeFullName, String projectId) {
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

    public int getId() {
        return this.id;
    }

    public void setProjectId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
