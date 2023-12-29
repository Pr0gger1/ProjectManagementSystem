package ru.sfedu.projectmanagement.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.Queries;
import ru.sfedu.projectmanagement.core.utils.types.NoData;
import ru.sfedu.projectmanagement.core.utils.types.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PostgresUtil {
    private final static Logger logger = LogManager.getLogger(PostgresUtil.class);

    /**
     *
     * @param connection database connection necessary for making row count extracting queries
     * @param table table name of entity that will be checked
     * @param uuid entity id
     * @return if entity with such id exists returns true else false
     * @throws SQLException throws exception if something goes wrong while executing query
     */
    public static boolean isRecordExists(Connection connection, String table, UUID uuid) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE id = '%s'", table, uuid);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int result =  resultSet.getInt(1);
        return result > 0;
    }

    /**
     *
     * @param connection database connection necessary for making row count extracting queries
     * @param employeeId employee id
     * @param projectId project id
     * @return Result object with execution code and message if it fails
     */
    public static Result<NoData> checkIfEmployeeBelongsToProject(Connection connection, UUID employeeId, UUID projectId) {
        String query = String.format(
                Queries.CHECK_EMPLOYEE_LINK_EXISTENCE_QUERY,
                Queries.EMPLOYEE_PROJECT_TABLE_NAME, employeeId, projectId
        );

        Result<NoData> result = new Result<>(ResultCode.SUCCESS);

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            int rowCount = 0;
            while (resultSet.next()) rowCount = resultSet.getInt(1);

            if (rowCount == 0)
                return new Result<>(ResultCode.ERROR, String.format(
                        Constants.EMPLOYEE_IS_NOT_LINKED_TO_PROJECT, employeeId)
                );
        }
        catch (SQLException exception) {
            logger.error("checkIfEmployeeBelongsToProject[2]: {}", exception.getMessage());
            result.setCode(ResultCode.ERROR);
            result.setMessage(exception.getMessage());
        }
        return result;
    }
}
