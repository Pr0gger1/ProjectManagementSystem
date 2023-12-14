package ru.sfedu.projectmanagement.core.model;

import jakarta.xml.bind.annotation.*;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.util.Objects;
import java.util.UUID;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmployeeProject")
public class EmployeeProjectObject implements Entity {
    @XmlTransient
    private EntityType entityType = EntityType.EmployeeProject;
    @XmlElement(name = "employee_id")
    private UUID employeeId;

    @XmlElement(name = "project_id")
    private UUID projectId;

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    private EmployeeProjectObject() {}
    public EmployeeProjectObject(UUID employeeId, UUID projectId) {
        this.employeeId = employeeId;
        this.projectId = projectId;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmployeeProjectObject that = (EmployeeProjectObject) object;
        return Objects.equals(employeeId, that.employeeId) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, projectId);
    }
}
