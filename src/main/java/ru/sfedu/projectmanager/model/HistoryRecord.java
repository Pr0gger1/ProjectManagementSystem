package ru.sfedu.projectmanager.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.enums.ActionStatus;

import java.util.Date;
import java.util.UUID;

public class HistoryRecord<T> {
    private static final Logger logger = LogManager.getLogger(HistoryRecord.class);
    private final UUID id;
    private String className;
    private String methodName;
    private Date createdAt;
    private String actor = Constants.ACTOR;
    private ActionStatus status;
    private Object object;

    public HistoryRecord(
            T object,
            String methodName,
            ActionStatus status
    ) {
        id = UUID.randomUUID();
        this.className = object.getClass().getSimpleName();
        this.object = object;
        this.createdAt = new Date();
        this.status = status;
        this.methodName = methodName;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public UUID getId() {
        return id;
    }

    public Document convertToDocument() {
        try {
            Document document = new Document();
            ObjectMapper objMapper = new ObjectMapper();

            document.put(Constants.MONGO_HISTORY_ID, id);
            document.put(Constants.MONGO_HISTORY_CLASSNAME, className);
            document.put(Constants.MONGO_HISTORY_METHOD_NAME, methodName);
            document.put(Constants.MONGO_HISTORY_CREATED_AT, createdAt);
            document.put(Constants.MONGO_HISTORY_ACTOR, Constants.ACTOR);
            document.put(Constants.MONGO_HISTORY_STATUS, status.toString());
            document.put(Constants.MONGO_HISTORY_OBJECT, objMapper.writeValueAsString(object));

            return document;
        }
        catch (JsonProcessingException error) {
            logger.error(error.getMessage());
            return null;
        }
    }
}
