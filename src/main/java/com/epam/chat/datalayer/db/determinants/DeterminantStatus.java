package com.epam.chat.datalayer.db.determinants;

import com.epam.chat.datalayer.dto.Status;

public class DeterminantStatus {
    private static final String LOGIN = "LOGIN";
    private static final String KICK = "KICK";
    private static final String LOGOUT = "LOGOUT";

    public Status selectStatus(String status) {
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
}
