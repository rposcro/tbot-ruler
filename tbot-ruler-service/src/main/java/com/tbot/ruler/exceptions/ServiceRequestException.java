package com.tbot.ruler.exceptions;

public class ServiceRequestException extends ServiceException {

    public ServiceRequestException(String message) {
        super(message);
    }

    public ServiceRequestException(String message, Object... messageArguments) {
        super(String.format(message, messageArguments));
    }

    public ServiceRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
