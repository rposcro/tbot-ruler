package com.tbot.ruler.exceptions;

public class ServiceExecutionException extends ServiceException {

    public ServiceExecutionException(String message) {
        super(message);
    }

    public ServiceExecutionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
