package com.tbot.ruler.exceptions;

public class CriticalException extends RulerException {

    public CriticalException(String message) {
        super(message);
    }

    public CriticalException(String message, Throwable t) {
        super(message, t);
    }
}
