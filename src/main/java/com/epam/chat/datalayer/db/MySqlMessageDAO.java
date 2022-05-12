package com.epam.chat.datalayer.db;

import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.db.connectionpool.ConnectionPool;
import com.epam.chat.datalayer.db.connectionpool.ConnectionPoolException;
import com.epam.chat.datalayer.db.determinants.DeterminantStatus;
import com.epam.chat.datalayer.dto.Message;
import com.epam.chat.providers.DBParameters;
import com.epam.chat.providers.QueriesProvider;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MySqlMessageDAO implements MessageDAO {
    private static final Logger logger = Logger.getLogger(MySqlMessageDAO.class);
    private static final int USER_FROM_POSITION = 1;
    private static final int TEXT_POSITION = 2;
    private static final int TIME_STAMP_POSITION = 3;
    private static final int STATUS_POSITION = 4;
    private static final int FIRST_ARGUMENT = 1;
    private static final int SECOND_ARGUMENT = 2;
    private static final int THIRD_ARGUMENT = 3;
    private static final int FOURTH_ARGUMENT = 4;
    private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String ERROR_CONNECTION = "Connection error";
    private static final String ERROR_DATA = "Data retrieval error";
    private final ConnectionPool connectionPool;
    private final DeterminantStatus determinantStatus;

    public MySqlMessageDAO() {
        connectionPool = ConnectionPool.getConnectionPool();
        determinantStatus = new DeterminantStatus();
    }

    @Override
    public void sendMessage(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_STAMP_FORMAT);
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.NEW_MESSAGE));
            preparedStatement.setString(FIRST_ARGUMENT, message.getUserFrom());
            preparedStatement.setString(SECOND_ARGUMENT, message.getText());
            preparedStatement.setString(THIRD_ARGUMENT, message.getTimeStamp().format(formatter));
            preparedStatement.setInt(FOURTH_ARGUMENT, message.getStatus().getId());
            preparedStatement.executeUpdate();
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        }  finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement);
            }
        }
    }

    @Override
    public List<Message> getLast(int count) {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.LAST_MESSAGES));
            preparedStatement.setInt(FIRST_ARGUMENT, count);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Message message = createMessage(resultSet.getString(USER_FROM_POSITION), resultSet.getString(TEXT_POSITION),
                        resultSet.getString(TIME_STAMP_POSITION), resultSet.getString(STATUS_POSITION));
                messages.add(message);
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        }  finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement, resultSet);
            }
        }
        return messages;
    }

    private Message createMessage(String userFrom, String text, String timeStamp, String status) {
        Message message = new Message();

        message.setUserFrom(userFrom);
        message.setText(text);
        message.setTimeStamp(LocalDateTime.parse(timeStamp,
                DateTimeFormatter.ofPattern(TIME_STAMP_FORMAT)));
        message.setStatus(determinantStatus.selectStatus(status));

        return message;
    }
}
