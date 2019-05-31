package com.tbot.ruler.plugins.jwavez;

public enum BasicSetValueMode {

    TOGGLE_VALUE,
    ON_OFF_VALUES;

    public static BasicSetValueMode of(String modeName) {
        switch(modeName) {
            case "toggle":
                return TOGGLE_VALUE;
            case "on-off":
                return ON_OFF_VALUES;
            default:
                throw new IllegalArgumentException("Unknown mode name: " + modeName);
        }
    }
}
