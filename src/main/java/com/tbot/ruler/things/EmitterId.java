package com.tbot.ruler.things;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class EmitterId {
    private final String value;

    public EmitterId(String value) {
        this.value = value;
    }
}
