package ru.sfedu.projectmanager.model.enums;

public enum RepositoryType {
    XML("xml"), CSV("csv"), RDB("rdb");
    final String type;

    RepositoryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
