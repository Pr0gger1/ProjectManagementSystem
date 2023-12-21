package ru.sfedu.projectmanagement.core.model;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.util.Objects;
import java.util.UUID;

public class TaskTag implements Entity {
    @CsvBindByName(column = "task_id")
    private UUID taskId;

    @CsvBindByName(column = "tag")
    private String tag;

    public TaskTag() {}

    public TaskTag(UUID taskId, String tag) {
        this.tag = tag;
        this.taskId = taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getTaskId() {
        return getId();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TaskTag taskTag = (TaskTag) object;
        return Objects.equals(taskId, taskTag.taskId) && Objects.equals(tag, taskTag.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, tag);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TaskTag;
    }

    @Override
    public UUID getId() {
        return taskId;
    }

    @Override
    public String toString() {
        return tag;
    }
}
