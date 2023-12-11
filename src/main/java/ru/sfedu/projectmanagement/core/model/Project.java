package ru.sfedu.projectmanagement.core.model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.xml.adapters.XmlLocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "project")
@XmlType(name = "Project")
public class Project implements Entity {
    @XmlTransient
    private final EntityType entityType = EntityType.Project;
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(XmlLocalDateTimeAdapter.class)
    private LocalDateTime deadline;

    @XmlElement(required = true)
    private String name;

    @XmlElement(nillable = true)
    private String description;

    @XmlAttribute(required = true)
    private String id;

    @XmlElement(required = true)
    private WorkStatus status = WorkStatus.IN_PROGRESS;

    @XmlElement(nillable = true)
    private Employee manager;

    @XmlElementWrapper(name = "team")
    @XmlElement(name = "employee", nillable = true)
    private ArrayList<Employee> team = new ArrayList<>();

    @XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task", nillable = true)
    private ArrayList<ProjectEntity> tasks = new ArrayList<>();

    @XmlElementWrapper(name = "documentations")
    @XmlElement(name = "documentation", nillable = true)
    private ArrayList<ProjectEntity> documentation = new ArrayList<>();

    @XmlElementWrapper(name = "bug_reports")
    @XmlElement(name = "bug_report", nillable = true)
    private ArrayList<ProjectEntity> bugReports = new ArrayList<>();

    @XmlElementWrapper(name = "events")
    @XmlElement(name = "event", nillable = true)
    private ArrayList<ProjectEntity> events = new ArrayList<>();

    public Project(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;

    }

    public Project() {
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
            ArrayList<ProjectEntity> documentation
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
    public UUID getManagerId() {
        if (manager == null)
            return null;
        return manager.getId();
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

    public ArrayList<ProjectEntity> getDocumentations() {
        return documentation;
    }

    public void setDocumentations(ArrayList<ProjectEntity> documentation) {
        if (documentation.stream().allMatch(entity -> entity instanceof Documentation))
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBugReports(ArrayList<ProjectEntity> bugReports) {
        this.bugReports = bugReports;
    }

    public void setEvents(ArrayList<ProjectEntity> events) {
        this.events = events;
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
