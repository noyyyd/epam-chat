package com.epam.chat.commands;

import com.epam.chat.datalayer.DBType;
import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.UserDAO;
import com.epam.chat.datalayer.dto.Message;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.User;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UserUnkick implements Command {
    private static final Logger logger = Logger.getLogger(UserKick.class);
    private static final String KICK_USERS = "kickUsers";
    private static final String EPAM = "@epam.com";
    private static final String NICK = "nick";
    private static final String MYSQL = "MYSQL";
    private static final String MESSAGES = "messages";
    private static final String USERS = "users";
    private static final String NICK_KICK_USER = "nickKickUser";
    private static final String CHAT_PATH = "/WEB-INF/pages/chat.jsp";
    private static final String ERROR_MESSAGE = "Dispatcher error";
    private final UserDAO mySqlUserDAO;
    private final MessageDAO mySqlMessageDAO;

    public UserUnkick() {
        this.mySqlUserDAO = DBType.getTypeByName(MYSQL).getDAOFactory().getUserDAO();
        this.mySqlMessageDAO = DBType.getTypeByName(MYSQL).getDAOFactory().getMessageDAO();
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        mySqlUserDAO.unkick(createUser(req.getParameter("nickKickUser")));
        mySqlUserDAO.logout(createUser(req.getParameter("nickKickUser")));

        List<Message> messages = mySqlMessageDAO.getLast(Integer.MAX_VALUE);
        Collections.reverse(messages);
        req.getServletContext().setAttribute(MESSAGES, messages);

        List<User> users = mySqlUserDAO.getAllLogged();
        Collections.reverse(users);
        req.getServletContext().setAttribute(USERS, users);

        List<User> kickUsers = mySqlUserDAO.getAllKicked();
        Collections.reverse(kickUsers);
        req.getServletContext().setAttribute(KICK_USERS, kickUsers);

        ServletContext servletContext = req.getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(CHAT_PATH);
        try {
            requestDispatcher.forward(req, resp);
        } catch (ServletException | IOException e) {
            logger.error(ERROR_MESSAGE, e);
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
