package ru.sfedu.projectmanagement.core.model.factory;
import ru.sfedu.projectmanagement.core.model.Entity;

import java.time.format.DateTimeFormatter;


public abstract class EntityBuilder {
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    protected final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public abstract Entity build(String[] args);
}
