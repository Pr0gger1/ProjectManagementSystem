package ru.sfedu.projectmanager.model.enums;

public enum BugStatus {
    OPENED("Открыт"),
    IN_PROGRESS("В работе"),
    CLOSED("Закрыт");

    private String status;

    BugStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
