package com.tbot.ruler.controller.exceptions;

import com.tbot.ruler.exceptions.RulerException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ControllerException extends RulerException {

    private final HttpStatus httpStatus;

    public ControllerException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
