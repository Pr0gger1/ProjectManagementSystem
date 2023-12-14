package ru.sfedu.projectmanagement.core.model;

import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.util.UUID;

public interface Entity {
     EntityType getEntityType();
     UUID getId();
}