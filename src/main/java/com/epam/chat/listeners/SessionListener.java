package com.epam.chat.listeners;

import com.epam.chat.commands.UserLogout;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
	private final static int MAX_INACTIVE_INTERVAL = 600;
	private static final String NICK = "nick";

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		UserLogout userLogout = new UserLogout();
		userLogout.logout((String) session.getAttribute(NICK));
	}

}
