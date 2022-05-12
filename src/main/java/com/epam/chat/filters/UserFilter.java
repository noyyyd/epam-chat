package com.epam.chat.filters;

import com.epam.chat.datalayer.DBType;
import com.epam.chat.datalayer.UserDAO;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.User;

public class UserFilter {
    private static final String EPAM = "@epam.com";
    private static final String MYSQL = "MYSQL";
    private static final int MIN_LENGTH_NICK = 3;
    private static final int MAX_LENGTH_NICK = 20;
    private final UserDAO mySqlUserDAO;

    public UserFilter() {
        this.mySqlUserDAO = DBType.getTypeByName(MYSQL).getDAOFactory().getUserDAO();
    }

    public boolean kickCheck(String nick) {
        User user = createUser(nick);

        return mySqlUserDAO.isKicked(user);
    }

    public boolean loginCheck(String nick) {
        User user = createUser(nick);

        return mySqlUserDAO.isLoggedIn(user);
    }

    public boolean nickCheck(String nick) {
        if (nick.length() < MIN_LENGTH_NICK || nick.length() > MAX_LENGTH_NICK) {
            return false;
        } else {
            return true;
        }
    }

    private Role definingRole(String nick) {
        if (nick.contains(EPAM)) {
            return Role.ADMIN;
        } else {
            return Role.USER;
        }
    }

    private User createUser(String nick) {
        User user = new User();
        user.setNick(nick);
        user.setRole(definingRole(nick));
        return user;
    }
}
