package ru.sfedu.projectmanagement.core.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUtil {
    public static boolean isRecordExists(
            Connection connection,
            String tableName,
            String columnName,
            Object value
    ) throws SQLException {
        String query = String.format(
                "SELECT COUNT(*) FROM %s WHERE %s = ?",
                tableName, columnName
        );

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(1, value);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("count") > 0;
    }
}
