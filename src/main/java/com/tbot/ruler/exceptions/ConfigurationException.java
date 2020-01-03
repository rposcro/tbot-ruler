package com.tbot.ruler.exceptions;

public class ConfigurationException extends RulerException {

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }
}
