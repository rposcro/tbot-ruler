package com.tbot.ruler.exceptions;

public class ServiceTimeoutException extends ServiceException {

    public ServiceTimeoutException(String message) {
        super(message);
    }

    public ServiceTimeoutException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
