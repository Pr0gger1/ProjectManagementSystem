package ru.sfedu.projectmanager.model;

import java.util.Calendar;

public class Event extends ProjectEntity {
    private Calendar startDate;
    private Calendar endDate;

    Event(
        String name,
        String description,
        int employeeId,
        String employeeFullName,
        String projectId,
        Calendar startDate,
        Calendar endDate
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
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
}
