package com.epam.chat.filters;

import com.epam.chat.datalayer.DBType;
import com.epam.chat.datalayer.UserDAO;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.Status;
import com.epam.chat.datalayer.dto.User;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;

/**
 * Implementation of servlet filter
 */
public class ChatFilter implements Filter {
    private static final String NICK = "nick";
    private static final String CMD = "cmd";
    private static final String LOGIN = "login";

    @Override
    public void destroy() {
        //TODO
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        UserFilter userFilter = new UserFilter();

        if (LOGIN.equals(request.getParameter(CMD))) {
            if (userFilter.kickCheck(request.getParameter(NICK))) {
                request.setAttribute("error", "You were kicked from chat");
                chain.doFilter(request, response);
            } else if (userFilter.loginCheck(request.getParameter(NICK))) {
                request.setAttribute("error", "User with this nickname already exists");
                chain.doFilter(request, response);
            } else if (!userFilter.nickCheck(request.getParameter(NICK))) {
                request.setAttribute("error", "Nickname must contain from 3 to 20 characters");
                chain.doFilter(request, response);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }

    }



    @Override
    public void init(FilterConfig config) throws ServletException {
        //TODO
    }
}
