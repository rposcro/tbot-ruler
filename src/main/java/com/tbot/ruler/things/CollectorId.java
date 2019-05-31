package com.tbot.ruler.things;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class CollectorId {
    private final String value;

    public CollectorId(String value) {
        this.value = value;
    }
}
