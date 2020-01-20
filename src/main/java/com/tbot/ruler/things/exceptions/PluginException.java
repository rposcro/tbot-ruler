package com.tbot.ruler.things.exceptions;

public class PluginException extends ThingException {

    public PluginException(String message) {
        super(message);
    };

    public PluginException(String message, Throwable throwable) {
        super(message, throwable);
    };
}
