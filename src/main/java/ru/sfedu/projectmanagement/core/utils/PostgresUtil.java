package ru.sfedu.projectmanagement.core.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PostgresUtil {
    public static boolean isRecordExists(Connection connection, String table, UUID uuid) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE id = '%s'", table, uuid);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int result =  resultSet.getInt("count");
        return result > 0;
    }
}
