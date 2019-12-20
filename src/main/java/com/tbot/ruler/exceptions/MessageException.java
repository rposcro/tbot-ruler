package com.tbot.ruler.exceptions;

public class MessageException extends RulerException {

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }

    public MessageException(String message) {
        super(message);
    }
}
