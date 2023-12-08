package ru.sfedu.projectmanagement.core.api;

import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BaseTest {
    static protected Project createProject(
            String id,
            String name,
            String description,
            WorkStatus status,
            LocalDateTime deadline,
            ArrayList<ProjectEntity> tasks,
            ArrayList<ProjectEntity> bugReports,
            ArrayList<ProjectEntity> events,
            ArrayList<ProjectEntity> documentations,
            ArrayList<Employee> team
    ) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setTasks(tasks);
        project.setEvents(events);
        project.setBugReports(bugReports);
        project.setDocumentations(documentations);
        project.setTeam(team);
        project.setStatus(status);
        project.setDeadline(deadline);
        
        return project;
    }
    
    static protected Task createTask(
            String projectId,
            UUID employeeId,
            String employeeFullName,
            String name,
            String description,
            String comment,
            WorkStatus status,
            ArrayList<String> tags,
            LocalDateTime completedAt,
            LocalDateTime deadline,
            Priority priority
    ) {
        Task task = new Task();
        task.setProjectId(projectId);
        task.setEmployeeFullName(employeeFullName);
        task.setEmployeeId(employeeId);
        task.setName(name);
        task.setDescription(description);
        task.setComment(comment);
        task.setStatus(status);
        task.setCompletedAt(completedAt);
        task.setTags(tags);
        task.setDeadline(deadline);
        task.setPriority(priority);
        
        return task;
    }

    static protected BugReport createBugReport(
            String name,
            String description,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            BugStatus status,
            Priority priority
    ) {
        BugReport bugReport = new BugReport();
        bugReport.setProjectId(projectId);
        bugReport.setPriority(priority);
        bugReport.setEmployeeId(employeeId);
        bugReport.setEmployeeFullName(employeeFullName);
        bugReport.setStatus(status);
        bugReport.setName(name);
        bugReport.setDescription(description);

        return bugReport;
    }

    static protected Event createEvent(
            String name,
            String description,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setProjectId(projectId);
        event.setEmployeeId(employeeId);
        event.setEmployeeFullName(employeeFullName);
        event.setEndDate(endDate);
        event.setStartDate(startDate);

        return event;
    }

    static protected Documentation createDocumentation(
            String name,
            String description,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            HashMap<String, String> body
    ) {
        Documentation documentation = new Documentation();
        documentation.setName(name);
        documentation.setProjectId(projectId);
        documentation.setDescription(description);
        documentation.setEmployeeId(employeeId);
        documentation.setEmployeeFullName(employeeFullName);
        documentation.setBody(body);
        return documentation;
    }

    static protected Employee createEmployee(
            String firstName,
            String lastName,
            String patronymic,
            LocalDate birthday,
            String phoneNumber,
            String position,
            String email
    ) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPatronymic(patronymic);
        employee.setBirthday(birthday);
        employee.setPhoneNumber(phoneNumber);
        employee.setPosition(position);
        employee.setEmail(email);

        return employee;
    }
}
