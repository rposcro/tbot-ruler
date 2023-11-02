package com.tbot.ruler.broker.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class Trigger {

    public static final Trigger TRIGGER = new Trigger();

    private Trigger() {}

    @JsonCreator
    public static Trigger trigger() {
        return TRIGGER;
    }
}
