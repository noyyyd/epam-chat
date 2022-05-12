package com.epam.chat.datalayer.xml;

import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.UserDAO;
import com.epam.chat.datalayer.dto.Message;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.Status;
import com.epam.chat.datalayer.dto.User;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class XMLUserDAO implements UserDAO {

    public static final String XML_ERROR = "Error when working with XML file";
    public static final String XML_SAVE_ERROR = "File save error";
    public static final String MESSAGE = "message";
    public static final String MESSAGES = "messages";
    public static final String USERS = "users";
    public static final String USER = "user";
    public static final String NICK = "nick";
    public static final String ROLE = "role";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    public static final String ENTERED_CHAT = "%s entered the chat";
    public static final String LEFT_CHAT = "%s left chat";
    public static final int NICK_POSITION = 1;
    public static final int TEXT_POSITION = 5;
    public static final int STATUS_POSITION = 7;
    private static final Logger logger = Logger.getLogger(XMLUserDAO.class);
    private MessageDAO xmlMessageDAO;
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public XMLUserDAO(MessageDAO xmlMessageDAO){
        this.xmlMessageDAO = xmlMessageDAO;
    }

    @Override
    public void login(User userToLogin) {
        Document document = createDocument();
        Node root = document.getElementsByTagName(USERS).item(0);

        if (!checkingUserExistence(userToLogin)) {
            Element user = document.createElement(USER);
            Element nick = document.createElement(NICK);
            nick.setTextContent(userToLogin.getNick());
            Element role = document.createElement(ROLE);
            role.setTextContent(userToLogin.getRole().toString());

            user.appendChild(nick);
            user.appendChild(role);

            root.appendChild(user);

            saveChanges(document);
        }

        createMessage(userToLogin.getNick(), String.format(ENTERED_CHAT, userToLogin.getNick()), Status.LOGIN);
    }

    private boolean checkingUserExistence(User user) {
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(USERS).item(0);
        NodeList loggedUser = root.getElementsByTagName(USER);
        for (int i = 0; i < loggedUser.getLength(); i++) {
            NodeList elements = loggedUser.item(i).getChildNodes();
            if (user.equals(createUser(elements))) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean isLoggedIn(User user) {
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(MESSAGES).item(0);
        NodeList messages = root.getElementsByTagName(MESSAGE);

        for (int i = messages.getLength() - 1; i >= 0; i--) {
            NodeList elements = messages.item(i).getChildNodes();

            if (user.getNick().equals(elements.item(NICK_POSITION).getTextContent()) &&
                    Status.LOGIN.toString().equals(elements.item(STATUS_POSITION).getTextContent())) {
                return true;
            }

            if (user.getNick().equals(elements.item(NICK_POSITION).getTextContent()) &&
                    Status.LOGOUT.toString().equals(elements.item(STATUS_POSITION).getTextContent())) {
                return false;
            }

            if (user.getNick().equals(elements.item(NICK_POSITION).getTextContent()) &&
                    Status.KICK.toString().equals(elements.item(STATUS_POSITION).getTextContent())) {
                return false;
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
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(MESSAGES).item(0);
        NodeList messages = root.getElementsByTagName(MESSAGE);

        for (int i = messages.getLength() - 1; i > 0; i--) {
            NodeList elements = messages.item(i).getChildNodes();

            if (user.getNick().equals(elements.item(NICK_POSITION).getTextContent()) &&
                    Status.KICK.toString().equals(elements.item(STATUS_POSITION).getTextContent())) {
                root.removeChild(messages.item(i));
                saveChanges(document);
            }
        }
    }

    @Override
    public void kick(User admin, User kickableUser) {
        createMessage(admin.getNick(), kickableUser.getNick(), Status.KICK);
    }

    @Override
    public boolean isKicked(User user) {
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(MESSAGES).item(0);
        NodeList messages = root.getElementsByTagName(MESSAGE);

        for (int i = messages.getLength() - 1; i > 0; i--) {
            NodeList elements = messages.item(i).getChildNodes();

            if (user.getNick().equals(elements.item(NICK_POSITION).getTextContent()) &&
                    Status.KICK.toString().equals(elements.item(STATUS_POSITION).getTextContent())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<User> getAllLogged() {
        List<User> loggedUsers = new ArrayList<>();
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(MESSAGES).item(0);
        NodeList messages = root.getElementsByTagName(MESSAGE);
        Map<String, Boolean> usersCheck = new HashMap<>();
        Set<String> keys = new HashSet<>();

        for (int i = messages.getLength() - 1; i >= 0; i--) {
            NodeList elements = messages.item(i).getChildNodes();
            keys.add(elements.item(NICK_POSITION).getTextContent());

            usersCheck.putIfAbsent(elements.item(NICK_POSITION).getTextContent(), true);

            if (elements.item(STATUS_POSITION).getTextContent().equals(Status.LOGOUT.toString())) {
                usersCheck.put(elements.item(NICK_POSITION).getTextContent(), false);
            }

            if (elements.item(STATUS_POSITION).getTextContent().equals(Status.KICK.toString())) {
                usersCheck.put(elements.item(TEXT_POSITION).getTextContent(), false);
            }
        }

        Iterator iterator = keys.iterator();

        for (int i = 0; i < usersCheck.size(); i++) {
            String nick = (String) iterator.next();
            if (usersCheck.get(nick)) {
                User user = new User();
                user.setNick(nick);
                user.setRole(getRole(nick));
                loggedUsers.add(user);
            }
        }

        return loggedUsers;
    }

    @Override
    public List<User> getAllKicked() {
        List<User> kickedUsers = new ArrayList<>();
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(MESSAGES).item(0);
        NodeList messages = root.getElementsByTagName(MESSAGE);

        for (int i = messages.getLength() - 1; i > 0; i--) {
            NodeList elements = messages.item(i).getChildNodes();

            if (elements.item(STATUS_POSITION).getTextContent().equals(Status.KICK.toString())) {
                User user = new User();
                user.setNick(elements.item(TEXT_POSITION).getTextContent());
                user.setRole(getRole(elements.item(TEXT_POSITION).getTextContent()));
                kickedUsers.add(user);
            }
        }

        return kickedUsers;
    }

    @Override
    public Role getRole(String nick) {
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(USERS).item(0);
        NodeList loggedUser = root.getElementsByTagName(USER);
        for (int i = 0; i < loggedUser.getLength(); i++) {
            NodeList elements = loggedUser.item(i).getChildNodes();
            User user = createUser(elements);
            if (nick.equals(user.getNick()) && user.getRole().equals(Role.ADMIN)) {
                return Role.ADMIN;
            }
        }

        return Role.USER;
    }

    private User createUser(NodeList list) {
        User user = new User();

        for (int i = 0; i < list.getLength(); i++) {
            if (NICK.equals(list.item(i).getNodeName())) {
                user.setNick(list.item(i).getTextContent().trim());
            } else if (ROLE.equals(list.item(i).getNodeName())) {
                switch (list.item(i).getTextContent().trim()) {
                    case ADMIN_ROLE:
                        user.setRole(Role.ADMIN);
                        break;
                    case USER_ROLE:
                        user.setRole(Role.USER);
                        break;
                    default:
                        break;
                }
            }
        }

        return user;
    }

    private void createMessage(String userFrom, String text, Status status) {
        Message message = new Message();
        message.setTimeStamp(LocalDateTime.now());
        message.setUserFrom(userFrom);
        message.setText(text);
        message.setStatus(status);

        xmlMessageDAO.sendMessage(message);
    }

    private void saveChanges(Document document) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream(XMLDAOFactory.XML_FILE_PATH);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (IOException | TransformerException e) {
            logger.error(XML_SAVE_ERROR, e);
            throw new XMLFileException(XML_SAVE_ERROR, e);
        }
    }

    private Document createDocument() {
        Document document = null;
        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            document = documentBuilder.parse(XMLDAOFactory.XML_FILE_PATH);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error(XML_ERROR, e);
            throw new XMLFileException(XML_ERROR, e);
        }
        return document;
    }
}
