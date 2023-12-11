package ru.sfedu.projectmanagement.core.utils;

import java.util.Optional;

public class PostgresUtil {
    public static String convertObjectToSqlString(Object object) {
        return Optional.ofNullable(object)
                .map(d -> String.format("'%s'", d))
                .orElse(null);
    }
}
