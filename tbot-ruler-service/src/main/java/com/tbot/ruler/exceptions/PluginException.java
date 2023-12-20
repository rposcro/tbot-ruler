package com.tbot.ruler.exceptions;

public class PluginException extends RulerException {

    public PluginException(String message) {
        super(message);
    };

    public PluginException(String message, Throwable throwable) {
        super(message, throwable);
    };
}
