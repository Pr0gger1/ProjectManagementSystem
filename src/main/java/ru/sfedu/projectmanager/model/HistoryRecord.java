package ru.sfedu.projectmanager.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.enums.ActionStatus;
import ru.sfedu.projectmanager.model.enums.ChangeType;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class HistoryRecord<T> {
    private static final Logger logger = LogManager.getLogger(HistoryRecord.class);
    private final UUID id;
    private String className;
    private String methodName;
    private Date createdAt;
    private String actor = Constants.MONGO_HISTORY_ACTOR;
    private ActionStatus status;
    private ChangeType changeType;
    private Object object;

    static {
        initJavaTimeModule();
    }


    public HistoryRecord(
            T object,
            String methodName,
            ActionStatus status,
            ChangeType changeType
    ) {
        id = UUID.randomUUID();
        this.className = object.getClass().getSimpleName();
        this.changeType = changeType;
        this.object = object;
        this.createdAt = new Date();
        this.status = status;
        this.methodName = methodName;
    }

    private static void initJavaTimeModule() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public String getClassName() {
        return className;
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
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
            objMapper.registerModule(new JavaTimeModule());

            document.put(Constants.MONGO_HISTORY_ID, id.toString());
            document.put(Constants.MONGO_HISTORY_CLASSNAME, className);
            document.put(Constants.MONGO_HISTORY_METHOD_NAME, methodName);
            document.put(Constants.MONGO_HISTORY_CREATED_AT, createdAt.toString());
            document.put(Constants.MONGO_HISTORY_ACTOR, Constants.MONGO_HISTORY_ACTOR);
            document.put(Constants.MONGO_HISTORY_STATUS, status.toString());
            document.put(Constants.MONGO_HISTORY_CHANGE_TYPE, changeType.toString());
            document.put(Constants.MONGO_HISTORY_OBJECT, objMapper.writeValueAsString(objMapper));

            return document;
        }
        catch (JsonProcessingException error) {
            logger.error(error.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            return String.format(
                    """
                    id: %s,
                    className: %s,
                    methodName: %s,
                    createdAt: %s,
                    status: %s,
                    changeType: %s
                    actor: %s,
                    object: %s
                    """,
                    id.toString(),
                    className, methodName,
                    createdAt.toString(),
                    status.toString(), actor,
                    changeType.toString(),
                    new ObjectMapper().writeValueAsString(object)
            );
        }
        catch (JsonProcessingException error) {
            logger.error(error.getMessage());
            return String.format(
                    """
                    id: %s,
                    className: %s,
                    methodName: %s,
                    createdAt: %s,
                    status: %s,
                    changeType: %s,
                    actor: %s,
                    object: %s
                    """,
                    id, className, methodName,
                    createdAt.toString(),
                    status.toString(), actor,
                    changeType.toString(),
                    object.toString()
            );
        }
    }

    @Override
    public boolean equals(Object object1) {
        if (this == object1) return true;
        if (object1 == null || getClass() != object1.getClass()) return false;
        HistoryRecord<?> record = (HistoryRecord<?>) object1;
        return Objects.equals(id, record.id) && Objects.equals(className, record.className) && Objects.equals(methodName, record.methodName) && Objects.equals(createdAt, record.createdAt) && Objects.equals(actor, record.actor) && status == record.status && changeType == record.changeType && Objects.equals(object, record.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, className, methodName, createdAt, actor, status, changeType, object);
    }


}
