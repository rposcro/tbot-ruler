package com.tbot.ruler.exceptions;

public class RulerException extends RuntimeException {

    public RulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RulerException(Throwable cause) {
        super(cause);
    }

    public RulerException(String message) {
        super(message);
    }
}
