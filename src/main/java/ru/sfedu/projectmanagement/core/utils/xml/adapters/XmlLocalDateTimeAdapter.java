package ru.sfedu.projectmanagement.core.utils.xml.adapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import ru.sfedu.projectmanagement.core.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XmlLocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.LOCAL_DATETIME_FORMAT);

    @Override
    public LocalDateTime unmarshal(String v) {
        return LocalDateTime.parse(v, formatter);
    }

    @Override
    public String marshal(LocalDateTime v) {
        return v.format(formatter);
    }
}