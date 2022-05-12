package com.epam.chat.datalayer.dto;

public enum Status {
    LOGIN(1),
    MESSAGE(2),
    KICK(3),
    LOGOUT(4);

    private int id;

    Status(int id) {
        this.id = id;

    }

    public int getId(){
        return id;
    }
}
