package com.epam.chat.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.epam.chat.commands.*;
import com.epam.chat.datalayer.dto.Status;

/**
 * Front controller implementation
 */
public class FrontController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CMD = "cmd";
    private static final String LOGOUT = "logout";
    private static final String LOGIN = "login";
    private static final String KICK = "kick";
    private static final String UNKICK = "unkick";
    private static final String MESSAGE = "message";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cmd = req.getParameter(CMD);
        Command command = defineCommand(cmd);
        command.execute(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected Command defineCommand(String commandName) {
        if (commandName == null) {
            return new MainPage();
        }
        switch (commandName) {
            case LOGIN:
                return new UserLogin();
            case LOGOUT:
                return new UserLogout();
            case KICK:
                return new UserKick();
            case UNKICK:
                return new UserUnkick();
            case MESSAGE:
                return new SendingMessage();
            default:
                return new MainPage();
        }
    }

}
