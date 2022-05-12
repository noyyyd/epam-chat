package com.epam.chat.datalayer.dto;

import java.util.Objects;

public class User {

    private String nick;
    private Role role;

    public String getNick() {
        return nick;
    }

    public Role getRole() {
        return role;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nick, user.nick) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "nick='" + nick + '\'' +
                ", role=" + role +
                '}';
    }
}
