package com.tbot.ruler.controller.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ControllerException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, Object... messageArgs) {
        super(String.format(message, messageArgs), HttpStatus.NOT_FOUND);
    }
}
