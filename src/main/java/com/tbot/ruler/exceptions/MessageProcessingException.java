package com.tbot.ruler.exceptions;

public class MessageProcessingException extends MessageException {

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MessageProcessingException(Throwable throwable) {
        super(throwable);
    }
}
