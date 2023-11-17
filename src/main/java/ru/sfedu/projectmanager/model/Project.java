package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.api.MongoHistoryProvider;
import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Project {
    private Calendar deadline;
    private final String name;
    private final String description;
    private final String projectId;
    private WorkStatus status = WorkStatus.IN_PROGRESS;
    private Employee manager;
    private ArrayList<Employee> team = new ArrayList<>();
    private ArrayList<ProjectEntity> tasks = new ArrayList<>();
    private ProjectEntity documentation;
    private final ArrayList<ProjectEntity> bugReports = new ArrayList<>();
    private final ArrayList<ProjectEntity> events = new ArrayList<>();

    public Project(String name, String description, String projectId) {
        this.name = name;
        this.description = description;
        this.projectId = projectId;

    }

    public Project(String name, String description, String projectId, Employee manager) {
        this.name = name;
        this.description = description;
        this.projectId = projectId;
        this.manager = manager;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(GregorianCalendar deadline) {
        this.deadline = deadline;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public ArrayList<ProjectEntity> getBugReports() {
        return bugReports;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public ProjectEntity getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ProjectEntity documentation) {
        if (documentation instanceof Documentation)
            this.documentation = documentation;
    }

    public ArrayList<ProjectEntity> getEvents() {
        return events;
    }

    public ArrayList<ProjectEntity> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<ProjectEntity> tasks) {
        if (tasks.stream().allMatch(entity -> entity instanceof Task))
            this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void deleteTask(int taskId) {}

    public ArrayList<Employee> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<Employee> team) {
        this.team = team;
    }

    public void addEmployee(Employee employee) {
        this.team.add(employee);
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
        return String.format(
        """
        Project {
        name: %s,
        description: %s,
        id: %s,
        status: %s,
        manager: %s,
        tasks: %s,
        team: %s\s
        }       \s
        """,
                name, description, projectId,
                status.toString(), manager == null ? "None" : manager.toString(),
                tasks.toString(), team.toString()
        );
    }
}
