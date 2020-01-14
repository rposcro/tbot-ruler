package com.tbot.ruler.exceptions;

import com.tbot.ruler.things.ItemId;
import lombok.Getter;

import java.util.Set;

public class ServiceUnavailableException extends ServiceException {

    @Getter
    private Set<ItemId> unavailableItems;

    public ServiceUnavailableException(String message, Set<ItemId> unavailableItems) {
        super(message);
        this.unavailableItems = unavailableItems;
    }
}
