package com.epam.chat.datalayer.db;

public class MySQLException extends RuntimeException {
    public MySQLException(String message, Exception e){
        super(message, e);
    }
}
