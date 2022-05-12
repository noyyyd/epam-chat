package com.epam.chat.datalayer.db;

import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.UserDAO;
import com.epam.chat.datalayer.db.connectionpool.ConnectionPool;
import com.epam.chat.datalayer.db.connectionpool.ConnectionPoolException;
import com.epam.chat.datalayer.db.determinants.DeterminantRole;
import com.epam.chat.datalayer.dto.Message;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.Status;
import com.epam.chat.datalayer.dto.User;
import com.epam.chat.providers.DBParameters;
import com.epam.chat.providers.QueriesProvider;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySqlUserDAO implements UserDAO {
    private static final Logger logger = Logger.getLogger(MySqlUserDAO.class);
    private static final int FIRST_ARGUMENT = 1;
    private static final int SECOND_ARGUMENT = 2;
    private static final int NICK_POSITION = 1;
    private static final int ROLE_POSITION = 2;
    private static final String ENTERED_CHAT = "%s entered the chat";
    private static final String LEFT_CHAT = "%s left chat";
    private static final String ERROR_CONNECTION = "Connection error";
    private static final String ERROR_DATA = "Data retrieval error";
    private final DeterminantRole determinantRole;
    private final ConnectionPool connectionPool;
    private final MySqlMessageDAO mySqlMessageDAO;

    public MySqlUserDAO(MessageDAO mySqlMessageDAO) {
        this.mySqlMessageDAO = (MySqlMessageDAO) mySqlMessageDAO;
        determinantRole = new DeterminantRole();
        connectionPool = ConnectionPool.getConnectionPool();
    }

    @Override
    public void login(User userToLogin) {
        if (!searchUser(userToLogin.getNick())) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = connectionPool.getConnection();
                preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.NEW_USER));
                preparedStatement.setString(FIRST_ARGUMENT, userToLogin.getNick());
                preparedStatement.setInt(SECOND_ARGUMENT, userToLogin.getRole().getId());

                preparedStatement.executeUpdate();
            } catch (ConnectionPoolException e) {
                logger.error(ERROR_CONNECTION, e);
                throw new MySQLException(ERROR_CONNECTION, e);
            } catch (SQLException e) {
                logger.error(ERROR_DATA, e);
                throw new MySQLException(ERROR_DATA, e);
            } finally {
                if (connection != null) {
                    connectionPool.closeConnection(connection, preparedStatement);
                }
            }
        }

        createMessage(userToLogin.getNick(), String.format(ENTERED_CHAT, userToLogin.getNick()), Status.LOGIN);
    }

    public boolean searchUser(String nick) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.USER_BY_NICK));
            preparedStatement.setString(FIRST_ARGUMENT, nick);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement, resultSet);
            }
        }
        return false;
    }

    @Override
    public boolean isLoggedIn(User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.LOGIN_CHECK));
            preparedStatement.setString(FIRST_ARGUMENT, user.getNick());
            preparedStatement.setString(SECOND_ARGUMENT, user.getNick());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getBoolean(1)) {
                    return true;
                }
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement, resultSet);
            }
        }
        return false;
    }

    @Override
    public void logout(User userToLogout) {
        createMessage(userToLogout.getNick(), String.format(LEFT_CHAT, userToLogout.getNick()), Status.LOGOUT);
    }

    @Override
    public void unkick(User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.UNKICK));
            preparedStatement.setString(FIRST_ARGUMENT, user.getNick());
            preparedStatement.executeUpdate();
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement);
            }
        }
    }

    @Override
    public void kick(User admin, User kickableUser) {
        createMessage(admin.getNick(), kickableUser.getNick(), Status.KICK);
    }

    @Override
    public boolean isKicked(User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.KICK_CHECK));
            preparedStatement.setString(FIRST_ARGUMENT, user.getNick());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getBoolean(1)) {
                    return true;
                }
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement, resultSet);
            }
        }
        return false;
    }

    @Override
    public List<User> getAllLogged() {
        List<User> loggedUsers = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(QueriesProvider.get(DBParameters.LOGGING_USERS));
            while (resultSet.next()) {
                User user = createUser(resultSet.getString(NICK_POSITION), resultSet.getString(ROLE_POSITION));
                loggedUsers.add(user);
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, statement, resultSet);
            }
        }
        return loggedUsers;
    }

    @Override
    public List<User> getAllKicked() {
        List<User> kickedUsers = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(QueriesProvider.get(DBParameters.KICK_USERS));
            while (resultSet.next()) {
                User user = createUser(resultSet.getString(NICK_POSITION), resultSet.getString(ROLE_POSITION));
                kickedUsers.add(user);
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, statement, resultSet);
            }
        }
        return kickedUsers;
    }

    @Override
    public Role getRole(String nick) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(QueriesProvider.get(DBParameters.ROLE_USER));
            preparedStatement.setString(FIRST_ARGUMENT, nick);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(Role.ADMIN.toString())) {
                    return Role.ADMIN;
                }
            }
        } catch (ConnectionPoolException e) {
            logger.error(ERROR_CONNECTION, e);
            throw new MySQLException(ERROR_CONNECTION, e);
        } catch (SQLException e) {
            logger.error(ERROR_DATA, e);
            throw new MySQLException(ERROR_DATA, e);
        } finally {
            if (connection != null) {
                connectionPool.closeConnection(connection, preparedStatement, resultSet);
            }
        }
        return Role.USER;
    }

    private User createUser(String nick, String role) {
        User user = new User();

        user.setNick(nick);
        user.setRole(determinantRole.selectRole(role));

        return user;
    }



    private void createMessage(String userFrom, String text, Status status) {
        Message message = new Message();
        message.setTimeStamp(LocalDateTime.now());
        message.setUserFrom(userFrom);
        message.setText(text);
        message.setStatus(status);

        mySqlMessageDAO.sendMessage(message);
    }
}
