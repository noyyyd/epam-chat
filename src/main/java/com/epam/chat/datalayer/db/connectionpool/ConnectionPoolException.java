package com.epam.chat.datalayer.db.connectionpool;

public class ConnectionPoolException extends Exception {
    public ConnectionPoolException(String message, Exception e){
        super(message, e);
    }
}
