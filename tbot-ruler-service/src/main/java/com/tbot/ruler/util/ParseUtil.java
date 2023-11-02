package com.tbot.ruler.util;

public class ParseUtil {

    public static int parseInt(String string) {
        if (string.startsWith("0x")) {
            return Integer.parseInt(string.substring(2), 16);
        }
        else if (string.startsWith("0b")) {
            return Integer.parseInt(string.substring(2), 2);
        }
        else if (string.startsWith("0") && string.length() > 1) {
            return Integer.parseInt(string.substring(1), 8);
        }
        else {
            return Integer.parseInt(string, 10);
        }
    }

    public static long parseLong(String string) {
        if (string.startsWith("0x")) {
            return Long.parseLong(string.substring(2), 16);
        }
        else if (string.startsWith("0b")) {
            return Long.parseLong(string.substring(2), 2);
        }
        else if (string.startsWith("0") && string.length() > 1) {
            return Long.parseLong(string.substring(1), 8);
        }
        else {
            return Long.parseLong(string, 10);
        }
    }
}
