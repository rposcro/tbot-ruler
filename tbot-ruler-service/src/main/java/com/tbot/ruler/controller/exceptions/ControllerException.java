package com.tbot.ruler.controller.exceptions;

import com.tbot.ruler.exceptions.RulerException;

public class ControllerException extends RulerException {

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
