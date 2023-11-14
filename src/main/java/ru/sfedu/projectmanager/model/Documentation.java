package ru.sfedu.projectmanager.model;

import java.util.HashMap;

public class Documentation extends ProjectEntity {
    private HashMap<String, String> body;

    Documentation(
        String name,
        String description,
        int employeeId,
        String employeeFullName,
        String projectId
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
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
        int employeeId,
        String employeeFullName,
        String projectId,
        HashMap<String, String> body
    ) {
        super(name, description, employeeId, employeeFullName, projectId);
        this.body = body;
    }

}
