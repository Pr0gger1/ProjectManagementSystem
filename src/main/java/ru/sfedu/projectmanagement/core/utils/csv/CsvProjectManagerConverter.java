package ru.sfedu.projectmanagement.core.utils.csv;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import ru.sfedu.projectmanagement.core.model.Employee;
import ru.sfedu.projectmanagement.core.model.Entity;

public class CsvProjectManagerConverter extends AbstractBeanField<Employee, String> {

    @Override
    protected String convertToWrite(Object manager) {
        return (manager != null) ? ((Entity) manager).getId().toString() : null;
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return null;
    }
}