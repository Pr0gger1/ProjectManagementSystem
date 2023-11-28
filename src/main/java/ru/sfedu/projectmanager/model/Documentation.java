package ru.sfedu.projectmanager.model;

import java.util.HashMap;
import java.util.UUID;

public class Documentation extends ProjectEntity {
    private HashMap<String, String> body;

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

    public HashMap<String, String> getBody() {
        return body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    public void addArticle(String articleTitle, String article) {
        body.put(articleTitle, article);
    }

    Documentation(
        String name,
        String description,
        UUID employeeId,
        String employeeFullName,
        String projectId,
        HashMap<String, String> body
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
        this.body = body;
    }

}
