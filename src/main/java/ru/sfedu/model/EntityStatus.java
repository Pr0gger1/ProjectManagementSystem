package ru.sfedu.model;

public enum EntityStatus {
    IN_PROGRESS("В процессе"),
    COMPLETED("Выполнено"),
    FROZEN("Отложено");

    private String status;

    EntityStatus(String s) {
        this.status = s;
    }

    public String getStatus() {
        return this.status;
    }
}
