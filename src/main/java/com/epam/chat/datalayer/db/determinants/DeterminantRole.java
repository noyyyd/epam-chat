package com.epam.chat.datalayer.db.determinants;

import com.epam.chat.datalayer.dto.Role;

public class DeterminantRole {
    private static final String ADMIN_ROLE = "ADMIN";

    public Role selectRole(String role) {
        if (role.equals(ADMIN_ROLE)) {
            return Role.ADMIN;
        } else {
            return Role.USER;
        }
    }
}
