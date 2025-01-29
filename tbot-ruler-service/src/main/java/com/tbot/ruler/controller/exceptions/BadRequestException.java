package com.tbot.ruler.controller.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ControllerException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Object... messageArgs) {
        super(String.format(message, messageArgs), HttpStatus.BAD_REQUEST);
    }
}
