package ru.sfedu.projectmanagement.core.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.utils.xml.adapters.XmlLocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "project")
@XmlType(name = "Project")
public class Project implements Entity {
    @CsvIgnore
    @XmlTransient
    private final EntityType entityType = EntityType.Project;

    @CsvBindByName(column = "deadline")
    @CsvDate
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(XmlLocalDateTimeAdapter.class)
    private LocalDateTime deadline;

    @CsvBindByName(column = "name",required = true)
    @XmlElement(required = true)
    private String name;

    @CsvBindByName(column = "description")
    @XmlElement(nillable = true)
    private String description;

    @CsvBindByName(column = "id", required = true)
    @XmlAttribute(required = true)
    private UUID id;

    @CsvBindByName(column = "status", required = true)
    @XmlElement(required = true)
    private WorkStatus status = WorkStatus.IN_PROGRESS;

    @CsvBindByName(column = "manager_id")
    @XmlElement(name = "manager_id", nillable = true)
    private UUID managerId;

    @CsvIgnore
    @XmlTransient
    private List<Employee> team = new ArrayList<>();

    @CsvIgnore
    @XmlTransient
    private List<ProjectEntity> tasks = new ArrayList<>();

    @CsvIgnore
    @XmlTransient
    private List<ProjectEntity> documentations = new ArrayList<>();

    @CsvIgnore
    @XmlTransient
    private List<ProjectEntity> bugReports = new ArrayList<>();

    @CsvIgnore
    @XmlTransient
    private List<ProjectEntity> events = new ArrayList<>();

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Project() {}
    public Project(Project project) {
        this(
            project.name,
            project.description,
            project.id,
            project.deadline,
            project.status,
            project.managerId,
            project.team,
            project.tasks,
            project.bugReports,
            project.events,
            project.documentations
        );
    }

    public Project(
            String name,
            String description,
            UUID id,
            LocalDateTime deadline,
            WorkStatus status,
            UUID managerId,
            List<Employee> team,
            List<ProjectEntity> tasks,
            List<ProjectEntity> bugReports,
            List<ProjectEntity> events,
            List<ProjectEntity> documentations
    ) {
        this.deadline = deadline;
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.managerId = managerId;
        this.team = team;
        this.tasks = tasks;
        this.documentations = documentations;
        this.events = events;
        this.bugReports = bugReports;
    }

    public Project(String name, String description, UUID id, UUID managerId) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.managerId = managerId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public List<ProjectEntity> getBugReports() {
        return bugReports;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<ProjectEntity> getDocumentations() {
        return documentations;
    }

    public void setDocumentations(ArrayList<Documentation> documentations) {
        documentations.forEach(this::addDocumentation);
    }

    public List<ProjectEntity> getEvents() {
        return events;
    }

    public List<Task> getTasks() {
        return tasks.stream().map(task -> (Task) task).toList();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks.stream()
                .map(task -> (ProjectEntity) task)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Employee> getTeam() {
        return team;
    }

    public void setTeam(List<Employee> team) {
        this.team = team;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addBugReport(BugReport bugReport) {
        this.bugReports.add(bugReport);
    }

    public void addDocumentation(Documentation documentation) {
        this.documentations.add(documentation);
    }

    public void addEmployee(Employee employee) {
        this.team.add(employee);
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

    public void setBugReports(ArrayList<BugReport> bugReports) {
        this.bugReports = bugReports.stream()
                .map(bugReport -> (ProjectEntity) bugReport)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setEvents(ArrayList<Event> events) {
        events.forEach(this::addEvent);
    }

    @Override
    public String toString() {
        return "Project{" +
                "deadline=" + deadline +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", managerId=" + managerId +
                ", team=" + team +
                ", tasks=" + tasks +
                ", documentation=" + documentations +
                ", bugReports=" + bugReports +
                ", events=" + events +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Project project = (Project) object;
        return Objects.equals(deadline, project.deadline) && Objects.equals(name, project.name) && Objects.equals(description, project.description) && Objects.equals(id, project.id) && status == project.status && Objects.equals(managerId, project.managerId) && Objects.equals(team, project.team) && Objects.equals(tasks, project.tasks) && Objects.equals(documentations, project.documentations) && Objects.equals(bugReports, project.bugReports) && Objects.equals(events, project.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deadline, name, description, id, status, managerId, team, tasks, documentations, bugReports, events);
    }
}
