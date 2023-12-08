package ru.sfedu.projectmanagement.core.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Documentation extends ProjectEntity {

    private HashMap<String, String> body;

    public Documentation() {
        super();
    }

    public Documentation(
            String name,
            String description,
            UUID employeeId,
            String employeeFullName,
            String projectId
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
    }

    public Documentation(
            String name,
            String description,
            HashMap<String, String> body,
            UUID employeeId,
            String employeeFullName,
            String projectId
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
        this.body = body;
    }

    public Documentation(
            String name,
            String description,
            UUID id,
            String projectId,
            UUID employeeId,
            String employeeFullName,
            LocalDateTime createdAt,
            HashMap<String, String> body
    ) {
        super(name, description, id, projectId, employeeId, employeeFullName, createdAt);
        this.body = body;
    }

    public HashMap<String, String> getBody() {
        return body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    public void addArticle(String articleTitle, String article) {
        body.put(articleTitle, article);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Documentation that = (Documentation) object;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "Documentation{" +
                "body=" + body +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", projectId='" + projectId + '\'' +
                ", employeeId=" + employeeId +
                ", employeeFullName='" + employeeFullName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
