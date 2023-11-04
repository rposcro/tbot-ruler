package com.tbot.ruler.exceptions;

public class RestRequestException extends RulerException {

    public RestRequestException(String message) {
        super(message);
    }

    public RestRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
