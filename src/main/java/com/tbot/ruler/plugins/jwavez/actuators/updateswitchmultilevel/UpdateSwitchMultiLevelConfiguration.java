package com.tbot.ruler.plugins.jwavez.actuators.updateswitchmultilevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSwitchMultiLevelConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(defaultValue = "0")
    private int pollStateInterval;
}
