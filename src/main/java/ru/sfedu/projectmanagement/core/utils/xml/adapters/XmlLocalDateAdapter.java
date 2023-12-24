package ru.sfedu.projectmanagement.core.utils.xml.adapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class XmlLocalDateAdapter extends XmlAdapter<String, LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * @param s input value from xml
     * @return parsed LocalDate instance
     */
    @Override
    public LocalDate unmarshal(String s)  {
        return LocalDate.parse(s, formatter);
    }

    /**
     * @param localDate input value of LocalDate from java object
     * @return formatted string
     */
    @Override
    public String marshal(LocalDate localDate) {
        return localDate.format(formatter);
    }
}
