package com.epam.chat.providers;

import java.util.ResourceBundle;

public class DBResourceProvider {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("connectionpool");

    public static String get(String key) {
        return resourceBundle.getString(key);
    }
}
