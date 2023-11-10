package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Project {
    private GregorianCalendar deadline;
    private final String name;
    private final String description;
    private final String projectId;
    private WorkStatus status;
    private int managerId;
    private ArrayList<Task> tasks;
    private ArrayList<Employee> team;

    public Project(String name, String description, String projectId) {
        this.name = name;
        this.description = description;
        this.projectId = projectId;
    }

    public GregorianCalendar getDeadline() {
        return deadline;
    }

    public void setDeadline(GregorianCalendar deadline) {
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
    public void deleteTask(int taskId) {}

    public ArrayList<Employee> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<Employee> team) {
        this.team = team;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("Project: %s\nDescription: %s\nManager ID: %s\n", name, description, managerId);
    }
}
