package com.tbot.ruler.things;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractItem implements Item {

    private String id;
    private String name;
    private String description;
}
