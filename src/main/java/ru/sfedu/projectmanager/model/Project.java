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
    private WorkStatus status;
    private Employee manager;
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Employee> team = new ArrayList<>();
    private final ArrayList<BugReport> bugReports = new ArrayList<>();
    private final ArrayList<Event> events = new ArrayList<>();
    private final ArrayList<HistoryRecord<?>> history = new ArrayList<>();

    public Project(String name, String description, String projectId) {
        this.name = name;
        this.description = description;
        this.projectId = projectId;

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

    public ArrayList<BugReport> getBugReports() {
        return bugReports;
    }

    public ArrayList<HistoryRecord<?>> getHistory() {
        return history;
    }

    public void addHistoryRecord(String dbName, HistoryRecord<?> record) {
        history.add(record);
        MongoHistoryProvider.save(dbName, record);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
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
    public void bindEntityToProject(ProjectEntity entity) {
        if (entity instanceof Task)
            tasks.add((Task) entity);
        if (entity instanceof BugReport)
            bugReports.add((BugReport) entity);
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
        managerId: %s,
        tasks: %s,
        team: %s\s
        }       \s
        """,
                name, description, projectId,
                status.toString(), manager.toString(),
                tasks.toString(), team.toString()
        );
    }
}
