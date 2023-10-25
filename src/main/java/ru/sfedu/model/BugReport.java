package ru.sfedu.model;

public class BugReport extends ProjectEntity {
    private Priority priority;
    private Employee author;
    private BugStatus status = BugStatus.OPENED;

    BugReport(String name, String description, Employee author) {
        super(name, description);
        this.author = author;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }
}
