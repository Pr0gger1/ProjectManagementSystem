package ru.sfedu.projectmanager.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Event extends ProjectEntity {
    private Calendar startDate;
    private Calendar endDate;

    public Event(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId,
            Calendar startDate,
            Calendar endDate
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
            Date createdAt,
            Calendar startDate,
            Calendar endDate
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
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
