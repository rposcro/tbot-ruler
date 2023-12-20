package com.tbot.ruler.console.exceptions;

public class ViewRenderException extends RuntimeException {

    public ViewRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewRenderException(String message) {
        super(message);
    }
}
