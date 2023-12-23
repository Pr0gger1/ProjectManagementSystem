package ru.sfedu.projectmanagement.core.model.factory;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EmployeeBuilder extends EntityBuilder {
    @Override
    public Employee build(String[] args) throws DateTimeParseException {
        if (args.length != Constants.EMPLOYEE_PRIMITIVE_PARAMETER_COUNT)
            throw new IllegalArgumentException(Constants.INVALID_PARAMETERS_MESSAGE);

        Employee employee = new Employee();
        employee.setFirstName(args[0]);
        employee.setLastName(args[1]);
        employee.setPatronymic(args[2].equals("null") ? null : args[2]);
        employee.setEmail(args[3]);
        employee.setBirthday(LocalDate.parse(args[4], dateFormatter));
        employee.setPhoneNumber(args[5]);
        employee.setPosition(args[6]);

        return employee;
    }
}
