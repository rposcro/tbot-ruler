package com.tbot.ruler.things;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ThingId {

    private final String value;

    public ThingId(String value) {
        this.value = value;
    }
}
