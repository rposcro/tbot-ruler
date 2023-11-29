package com.tbot.ruler.exceptions;

public class LifecycleException extends RulerException {

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(String message, Object... messageArguments) {
        super(String.format(message, messageArguments));
    }
}
