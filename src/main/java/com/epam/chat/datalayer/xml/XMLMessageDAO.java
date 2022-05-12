package com.epam.chat.datalayer.xml;

import com.epam.chat.datalayer.MessageDAO;
import com.epam.chat.datalayer.dto.Message;
import com.epam.chat.datalayer.dto.Status;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XMLMessageDAO implements MessageDAO {

    public static final String MESSAGE = "message";
    public static final String MESSAGES = "messages";
    public static final String USER_FROM = "user_from";
    public static final String TIME_STAMP = "time_stamp";
    public static final String TEXT = "text";
    public static final String STATUS = "status";
    public static final String LOGIN = "LOGIN";
    public static final String KICK = "KICK";
    public static final String LOGOUT = "LOGOUT";
    public static final String XML_ERROR = "Error when working with XML file";
    public static final String XML_SAVE_ERROR = "File save error";
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Logger logger = Logger.getLogger(XMLMessageDAO.class);
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    @Override
    public List<Message> getLast(int count) {
        List<Message> messages = new ArrayList<>();
        Document document = createDocument();
        Element root = (Element) document.getElementsByTagName(MESSAGES).item(0);
        NodeList messageElements = root.getElementsByTagName(MESSAGE);

        if (count > messageElements.getLength()) {
            count = messageElements.getLength();
        }

        for (int i = 0; i < count; i++) {
            NodeList elements = messageElements.item(i).getChildNodes();
            messages.add(parseMessage(elements));
        }

        return messages;
    }

    private Message parseMessage(NodeList list) {
        Message message = new Message();

        for (int i = 0; i < list.getLength(); i++) {
            switch (list.item(i).getNodeName()) {
                case USER_FROM:
                    message.setUserFrom(list.item(i).getTextContent().trim());
                    break;
                case TIME_STAMP:
                    message.setTimeStamp(LocalDateTime.parse(list.item(i).getTextContent().trim(),
                            DateTimeFormatter.ofPattern(TIME_STAMP_FORMAT)));
                    break;
                case TEXT:
                    message.setText(list.item(i).getTextContent().trim());
                    break;
                case STATUS:
                    message.setStatus(selectStatus(list.item(i).getTextContent().trim()));
                    break;
                default:
                    break;
            }
        }
        return message;
    }

    private Status selectStatus(String status) {
        switch (status) {
            case LOGIN:
                return Status.LOGIN;
            case KICK:
                return Status.KICK;
            case LOGOUT:
                return Status.LOGOUT;
            default:
                return Status.MESSAGE;
        }
    }

    @Override
    public void sendMessage(Message message) {
        Document document = createDocument();

        Node root = document.getElementsByTagName(MESSAGES).item(0);

        root.appendChild(buildMessage(document, message));

        saveChanges(document);

    }

    private Element buildMessage(Document document, Message message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_STAMP_FORMAT);
        Element messageElement = document.createElement(MESSAGE);
        Element userFrom = document.createElement(USER_FROM);
        userFrom.setTextContent(message.getUserFrom());
        Element timeStamp = document.createElement(TIME_STAMP);
        timeStamp.setTextContent(message.getTimeStamp().format(formatter));
        Element text = document.createElement(TEXT);
        text.setTextContent(message.getText());
        Element status = document.createElement(STATUS);
        status.setTextContent(message.getStatus().toString());

        messageElement.appendChild(userFrom);
        messageElement.appendChild(timeStamp);
        messageElement.appendChild(text);
        messageElement.appendChild(status);

        return messageElement;
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

    public Document createDocument() {
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
