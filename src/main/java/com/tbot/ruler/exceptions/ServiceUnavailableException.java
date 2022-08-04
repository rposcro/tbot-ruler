package com.tbot.ruler.exceptions;

import lombok.Getter;

import java.util.Set;

public class ServiceUnavailableException extends ServiceException {

    @Getter
    private Set<String> unavailableItems;

    public ServiceUnavailableException(String message, Set<String> unavailableItems) {
        super(message);
        this.unavailableItems = unavailableItems;
    }
}
