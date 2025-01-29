package com.tbot.ruler.exceptions;

public class RestClientRequestException extends RulerException {

    public RestClientRequestException(String message) {
        super(message);
    }

    public RestClientRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
