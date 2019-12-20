package com.tbot.ruler.things;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractItem implements Item {

    private ItemId id;
    private String name;
    private String description;
}
