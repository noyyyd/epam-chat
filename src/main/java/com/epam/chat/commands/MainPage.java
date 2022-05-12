package com.epam.chat.commands;

import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainPage implements Command {
    private static final Logger logger = Logger.getLogger(MainPage.class);
    private static final String LOGIN_PATH = "/WEB-INF/pages/login.jsp";
    private static final String ERROR_MESSAGE = "Dispatcher error";
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext servletContext = req.getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(LOGIN_PATH);
        try {
            requestDispatcher.forward(req, resp);
        } catch (ServletException | IOException e) {
            logger.error(ERROR_MESSAGE, e);
        }
    }
}
