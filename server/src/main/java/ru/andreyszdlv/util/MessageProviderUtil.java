package ru.andreyszdlv.util;

import java.util.ResourceBundle;

public class MessageProviderUtil {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    public static String getMessage(String key, Object... args) {
        return String.format(bundle.getString(key), args);
    }
}