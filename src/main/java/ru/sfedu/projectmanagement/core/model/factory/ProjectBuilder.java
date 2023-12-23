package ru.sfedu.projectmanagement.core.model.factory;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Project;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class ProjectBuilder extends EntityBuilder {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    /**
     * @param args project fields which includes name, description, manager id, project status, deadline.
     *             The order of the fields is important
     * @return instance of Project
     * @throws IllegalArgumentException throws an exception if manager id is not valid
     * @throws DateTimeParseException throws an exception if datetime format is not valid
     */
    public Project build(String[] args) throws IllegalArgumentException, DateTimeParseException {
        if (args.length != Constants.PROJECT_PRIMITIVE_PARAMETER_COUNT)
            throw new IllegalArgumentException(Constants.INVALID_PARAMETERS_MESSAGE);

        Project project = new Project();

        project.setName(args[0]);
        project.setDescription(args[1]);
        project.setManagerId(args[2].equals("null") ? null : UUID.fromString(args[2]));
        project.setStatus(WorkStatus.valueOf(args[3].toUpperCase()));
        project.setDeadline(LocalDateTime.parse(args[4], dateFormatter));

        return project;
    }
}
