package ru.sfedu.projectmanagement.core.model;

import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.util.UUID;

public class ManagerProjectObject extends EmployeeProjectObject {
    public ManagerProjectObject() {
        super();
        setEntityType(EntityType.ManagerProject);
    }

    public ManagerProjectObject(UUID employeeId, UUID projectId) {
        super(employeeId, projectId);
    }
}
