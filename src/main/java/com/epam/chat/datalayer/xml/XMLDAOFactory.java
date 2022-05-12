package com.epam.chat.datalayer.xml;

import com.epam.chat.datalayer.DAOFactory;
import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.UserDAO;

public class XMLDAOFactory extends DAOFactory {

    public static final String XML_FILE_PATH = "src/main/webapp/WEB-INF/chat_web.xml";

    @Override
    public MessageDAO getMessageDAO() {
        return new XMLMessageDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new XMLUserDAO(getMessageDAO());
    }

}
