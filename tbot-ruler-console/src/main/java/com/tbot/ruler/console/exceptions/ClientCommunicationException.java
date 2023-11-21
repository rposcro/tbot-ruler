package com.tbot.ruler.console.exceptions;

import lombok.Getter;

@Getter
public class ClientCommunicationException extends RuntimeException {

    private int statusCode;

    public ClientCommunicationException(String message) {
        super(message);
    }

    public ClientCommunicationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ClientCommunicationException(int statusCode, String message, Object... messageArgs) {
        super(String.format(message, messageArgs));
        this.statusCode = statusCode;
    }

    public ClientCommunicationException(String message, Throwable t) {
        super(message, t);
    }
}
