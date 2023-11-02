package com.tbot.ruler.exceptions;

public class ServiceException extends RulerException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
