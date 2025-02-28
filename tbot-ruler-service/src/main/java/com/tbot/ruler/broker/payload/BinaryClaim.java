package com.tbot.ruler.broker.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum BinaryClaim {

    SET_ON,
    SET_OFF,
    TOGGLE;

    public boolean isToggle() {
        return this == BinaryClaim.TOGGLE;
    }

    @JsonIgnore
    public boolean isSetOn() {
        return this == BinaryClaim.SET_ON;
    }

    @JsonIgnore
    public boolean isSetOff() {
        return this == BinaryClaim.SET_OFF;
    }

    @JsonCreator
    public static BinaryClaim of(@JsonProperty("on") boolean on) {
        return on ? SET_ON : SET_OFF;
    }
}
