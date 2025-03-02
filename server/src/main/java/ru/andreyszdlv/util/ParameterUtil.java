package ru.andreyszdlv.util;

public class ParameterUtil {
    public static String extractValueByPrefix(String param, String prefix) {
        if (param.startsWith(prefix)) {
            return param.substring(prefix.length());
        }
        return null;
    }
}