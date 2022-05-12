package com.epam.chat.datalayer.xml;

public class XMLFileException extends RuntimeException {
    public XMLFileException(String message, Exception e) {
        super(message, e);
    }
}
