package ru.sfedu.projectmanagement.core.model.builders;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Task;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskBuilder extends EntityBuilder {
    private final ArrayList<String> tags = new ArrayList<>();

    @Override
    public Task build(String[] args) throws IllegalArgumentException {
        if (args.length != Constants.TASK_PRIMITIVE_PARAMETER_COUNT)
            throw new IllegalArgumentException(Constants.INVALID_PARAMETERS_MESSAGE);

        Task task = new Task();
        task.setName(args[0]);
        task.setDescription(args[1]);
        task.setStatus(WorkStatus.valueOf(args[2].toUpperCase()));
        task.setTags(tags);
        task.setComment(args[3].equals("null") ? null : args[3]);
        task.setProjectId(UUID.fromString(args[4]));
        task.setCompletedAt(args[5].equals("null") ? null : LocalDateTime.parse(args[5], dateTimeFormatter));
        task.setDeadline(LocalDateTime.parse(args[6], dateTimeFormatter));
        task.setPriority(Priority.valueOf(args[7].toUpperCase()));
        task.setEmployeeId(UUID.fromString(args[8]));
        task.setEmployeeFullName(args[9]);

        return task;
    }

    public TaskBuilder setTags(String[] tags) {
        this.tags.addAll(List.of(tags));
        return this;
    }
}
