package ru.sfedu.projectmanagement.core.model;

import ru.sfedu.projectmanagement.core.model.enums.EntityType;

public interface Entity {
     EntityType getEntityType();
     Object getId();
}