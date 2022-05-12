package com.epam.chat.commands;

import com.epam.chat.datalayer.DBType;
import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.dto.Message;
import com.epam.chat.datalayer.dto.Status;
import com.epam.chat.filters.UserFilter;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class SendingMessage implements Command {
    private static final Logger logger = Logger.getLogger(SendingMessage.class);
    private static final String ERROR_MESSAGE = "Dispatcher error";
    private static final String MYSQL = "MYSQL";
    private static final String NICK = "nick";
    private static final String TEXT = "text";
    private static final String MESSAGES = "messages";
    private static final String CHAT_PATH = "/WEB-INF/pages/chat.jsp";
    private static final String LOGIN_PATH = "/WEB-INF/pages/login.jsp";
    private final MessageDAO mySqlMessageDAO;
    private UserFilter userFilter;

    public SendingMessage() {
        this.mySqlMessageDAO = DBType.getTypeByName(MYSQL).getDAOFactory().getMessageDAO();
        this.userFilter = new UserFilter();
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        if (userFilter.kickCheck((String) req.getSession().getAttribute(NICK))) {
            req.setAttribute("error", "You were kicked from chat");
            ServletContext servletContext = req.getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(LOGIN_PATH);
            try {
                requestDispatcher.forward(req, resp);
            } catch (ServletException | IOException e) {
                logger.error(ERROR_MESSAGE, e);
            }
        }

        Message message = new Message();
        message.setUserFrom((String) req.getSession().getAttribute(NICK));
        message.setStatus(Status.MESSAGE);
        message.setText(req.getParameter(TEXT));
        message.setTimeStamp(LocalDateTime.now());
        mySqlMessageDAO.sendMessage(message);

        List<Message> messages = mySqlMessageDAO.getLast(Integer.MAX_VALUE);
        Collections.reverse(messages);
        req.getServletContext().setAttribute(MESSAGES, messages);

        ServletContext servletContext = req.getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(CHAT_PATH);
        try {
            requestDispatcher.forward(req, resp);
        } catch (ServletException | IOException e) {
            logger.error(ERROR_MESSAGE, e);
        }
    }
}
