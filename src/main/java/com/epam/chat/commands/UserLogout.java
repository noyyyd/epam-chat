package com.epam.chat.commands;

import com.epam.chat.datalayer.DBType;
import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.UserDAO;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.User;
import com.epam.chat.datalayer.xml.XMLMessageDAO;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserLogout implements Command {
    private static final Logger logger = Logger.getLogger(UserLogout.class);
    private static final String EPAM = "@epam.com";
    private static final String MYSQL = "MYSQL";
    private static final String NICK = "nick";
    private static final String PATH_LOGIN = "/login";
    private static final String ERROR_MESSAGE = "Redirect error";
    private final UserDAO mySqlUserDAO;

    public UserLogout() {
        this.mySqlUserDAO = DBType.getTypeByName(MYSQL).getDAOFactory().getUserDAO();
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        mySqlUserDAO.logout(createUser((String) req.getSession().getAttribute(NICK)));
        req.getSession().invalidate();
        try {
            resp.sendRedirect(PATH_LOGIN);
        } catch (IOException e) {
            logger.error(ERROR_MESSAGE, e);
        }
    }

    public void logout(String nick){
        mySqlUserDAO.logout(createUser(nick));
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
