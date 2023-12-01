package ru.sfedu.projectmanager.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Event extends ProjectEntity {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Event(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(
            String name,
            String description,
            UUID id,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            LocalDateTime createdAt,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Event event = (Event) object;
        return Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
