package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchMultilevelConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(defaultValue = "0")
    private int switchDuration;
    @JsonProperty(defaultValue = "0")
    private int pollStateInterval;
}
