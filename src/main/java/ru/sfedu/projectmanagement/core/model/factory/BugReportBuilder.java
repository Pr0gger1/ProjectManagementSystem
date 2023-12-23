package ru.sfedu.projectmanagement.core.model.factory;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.BugReport;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.Priority;

import java.time.format.DateTimeParseException;
import java.util.UUID;

public class BugReportBuilder extends EntityBuilder {
    @Override
    public BugReport build(String[] args) throws IllegalArgumentException, DateTimeParseException {
        if (args.length != Constants.BUG_REPORT_PRIMITIVE_PARAMETER_COUNT)
            throw new IllegalArgumentException(Constants.INVALID_PARAMETERS_MESSAGE);

        BugReport bugReport = new BugReport();
        bugReport.setName(args[0]);
        bugReport.setDescription(args[1]);
        bugReport.setStatus(BugStatus.valueOf(args[2].toUpperCase()));
        bugReport.setPriority(Priority.valueOf(args[3].toUpperCase()));
        bugReport.setProjectId(UUID.fromString(args[4]));
        bugReport.setEmployeeId(UUID.fromString(args[5]));
        bugReport.setEmployeeFullName(args[6]);

        return bugReport;
    }
}
