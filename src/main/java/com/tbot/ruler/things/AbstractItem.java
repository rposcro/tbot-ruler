package com.tbot.ruler.things;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractItem<T extends ItemId> implements Item<T> {

    private T id;
    private String name;
    private String description;
}
