package com.epam.chat;

import com.epam.chat.datalayer.DBType;
import com.epam.chat.datalayer.db.MySqlMessageDAO;
import com.epam.chat.datalayer.db.MySqlUserDAO;
import com.epam.chat.datalayer.dto.Role;
import com.epam.chat.datalayer.dto.User;

public class Main {
    public static void main(String[] args) {
        MySqlUserDAO mySqlUserDAO = (MySqlUserDAO) DBType.getTypeByName("MYSQL").getDAOFactory().getUserDAO();
        //MySqlMessageDAO mySqlMessageDAO = (MySqlMessageDAO) DBType.getTypeByName("MYSQL").getDAOFactory().getMessageDAO();

        User user = new User();
        user.setRole(Role.ADMIN);
        user.setNick("Alexandr");

        User user1 = new User();
        user1.setNick("admin@epam.com");
        user1.setRole(Role.ADMIN);

        User user2 = new User();
        user2.setNick("Dima");
        user2.setRole(Role.USER);

        User user3 = new User();
        user3.setNick("semen");
        user3.setRole(Role.USER);

        User user4 = new User();
        user4.setNick("hi");
        user4.setRole(Role.USER);

        User user5 = new User();
        user5.setNick("Michael");
        user5.setRole(Role.USER);
        mySqlUserDAO.logout(user1);
        //mySqlUserDAO.logout(user2);
        //mySqlUserDAO.logout(user3);
        //mySqlUserDAO.logout(user4);
        //mySqlUserDAO.logout(user5);
        //mySqlUserDAO.unkick(user3);
        //mySqlUserDAO.login(user);
        //mySqlUserDAO.login(user2);
        //mySqlUserDAO.login(user3);
        System.out.println(mySqlUserDAO.getAllLogged());
        //System.out.println(mySqlUserDAO.getAllKicked());
        //System.out.println(mySqlUserDAO.isLoggedIn(user));
        //System.out.println(mySqlUserDAO.isKicked(user));
        //System.out.println(mySqlMessageDAO.getLast(5).toString());
    }
}
