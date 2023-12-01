package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.model.enums.WorkStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Project {
    private LocalDateTime deadline;
    private final String name;
    private final String description;
    private final String id;
    private WorkStatus status = WorkStatus.IN_PROGRESS;
    private Employee manager;
    private ArrayList<Employee> team = new ArrayList<>();
    private ArrayList<ProjectEntity> tasks = new ArrayList<>();
    private ProjectEntity documentation;
    private ArrayList<ProjectEntity> bugReports = new ArrayList<>();
    private ArrayList<ProjectEntity> events = new ArrayList<>();

    public Project(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;

    }

    public Project(
            String name,
            String description,
            String id,
            LocalDateTime deadline,
            WorkStatus status,
            Employee manager,
            ArrayList<Employee> team,
            ArrayList<ProjectEntity> tasks,
            ArrayList<ProjectEntity> bugReports,
            ArrayList<ProjectEntity> events,
            ProjectEntity documentation
    ) {
        this.deadline = deadline;
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.manager = manager;
        this.team = team;
        this.tasks = tasks;
        this.documentation = documentation;
        this.events = events;
        this.bugReports = bugReports;
    }

    public Project(String name, String description, String id, Employee manager) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.manager = manager;
    }

    public LocalDateTime getDeadline() {
        return deadline;
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

    public void setDeadline(LocalDateTime deadline) {
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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Project{" +
                "deadline=" + deadline +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", manager=" + manager +
                ", team=" + team +
                ", tasks=" + tasks +
                ", documentation=" + documentation +
                ", bugReports=" + bugReports +
                ", events=" + events +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Project project = (Project) object;
        return Objects.equals(deadline, project.deadline) && Objects.equals(name, project.name) && Objects.equals(description, project.description) && Objects.equals(id, project.id) && status == project.status && Objects.equals(manager, project.manager) && Objects.equals(team, project.team) && Objects.equals(tasks, project.tasks) && Objects.equals(documentation, project.documentation) && Objects.equals(bugReports, project.bugReports) && Objects.equals(events, project.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deadline, name, description, id, status, manager, team, tasks, documentation, bugReports, events);
    }
}
