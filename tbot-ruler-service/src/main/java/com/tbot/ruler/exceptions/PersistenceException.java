package com.tbot.ruler.exceptions;

public class PersistenceException extends RulerException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
