package ru.sfedu.projectmanagement.core.model.factory;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Event;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventBuilder extends EntityBuilder {
    @Override
    public Event build(String[] args) throws IllegalArgumentException {
        if (args.length != Constants.EVENT_PRIMITIVE_PARAMETER_COUNT)
            throw new IllegalArgumentException(Constants.INVALID_PARAMETERS_MESSAGE);

        Event event = new Event();
        event.setName(args[0]);
        event.setDescription(args[1]);
        event.setProjectId(UUID.fromString(args[2]));
        event.setEmployeeId(UUID.fromString(args[3]));
        event.setEmployeeFullName(args[4]);
        event.setStartDate(LocalDateTime.parse(args[5], dateTimeFormatter));
        event.setEndDate(LocalDateTime.parse(args[6], dateTimeFormatter));

        return event;
    }
}
