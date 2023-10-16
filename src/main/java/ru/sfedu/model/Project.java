package ru.sfedu.model;

import java.util.Date;

public class Project extends ManagementEntity {
    private Date deadline;
    private EntityStatus status;
    private int managerId;
    private Task[] tasks;
    private Employee[] team;

    Project(String name, String description) {
        super(name, description);
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }

    public Employee[] getTeam() {
        return team;
    }

    public void setTeam(Employee[] team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return String.format("Project: %s\nDescription: %s\nManager ID: %s\n", name, description, managerId);
    }
}
