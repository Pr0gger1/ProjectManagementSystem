package ru.sfedu.model;

public enum Priority {
    LOW("Низкий"),
    MEDIUM("Средний"),
    HIGH("Высокий");

    private String priority;

    Priority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }
}
