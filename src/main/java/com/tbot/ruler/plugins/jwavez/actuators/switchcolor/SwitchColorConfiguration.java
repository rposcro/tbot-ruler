package com.tbot.ruler.plugins.jwavez.actuators.switchcolor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchColorConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(defaultValue = "2")
    private int switchDuration;
    @JsonProperty(required = true)
    private String colorMode;
}
