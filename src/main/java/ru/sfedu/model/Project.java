package ru.sfedu.model;

import java.util.ArrayList;
import java.util.Date;

public class Project extends ProjectEntity {
    private Date deadline;
    private EntityStatus status;
    private int managerId;
    private ArrayList<Task> tasks;
    private ArrayList<Employee> team;

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

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public ArrayList<Employee> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<Employee> team) {
        this.team = team;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return String.format("Project: %s\nDescription: %s\nManager ID: %s\n", name, description, managerId);
    }
}
