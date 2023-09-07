package com.tbot.ruler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public enum BinaryStateClaim {

    SET_ON,
    SET_OFF,
    TOGGLE;

    public boolean isToggle() {
        return this == BinaryStateClaim.TOGGLE;
    }

    @JsonIgnore
    public boolean isSetOn() {
        return this == BinaryStateClaim.SET_ON;
    }

    @JsonIgnore
    public boolean isSetOff() {
        return this == BinaryStateClaim.SET_OFF;
    }
}
