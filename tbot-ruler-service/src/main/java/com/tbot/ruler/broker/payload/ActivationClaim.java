package com.tbot.ruler.broker.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

public enum ActivationClaim {

    ACTIVATE,
    DEACTIVATE,
    TOGGLE;

    public boolean isToggle() {
        return this == ActivationClaim.TOGGLE;
    }

    @JsonIgnore
    public boolean isActivate() {
        return this == ActivationClaim.ACTIVATE;
    }

    @JsonIgnore
    public boolean isDeactivate() {
        return this == ActivationClaim.DEACTIVATE;
    }
}
