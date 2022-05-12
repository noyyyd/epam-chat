package com.epam.chat.datalayer.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {

    private String text;
    private String userFrom;
    private LocalDateTime timeStamp;
    private Status status;

    public String getText() {
        return text;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", userFrom='" + userFrom + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(text, message.text) && Objects.equals(userFrom, message.userFrom) && Objects.equals(timeStamp, message.timeStamp) && Objects.equals(status, message.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, userFrom, timeStamp, status);
    }
}
