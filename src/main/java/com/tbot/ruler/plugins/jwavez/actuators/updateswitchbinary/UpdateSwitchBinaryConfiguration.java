package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSwitchBinaryConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(defaultValue = "0")
    private int endPointId;
    @JsonProperty(defaultValue = "0")
    private int pollStateInterval;
}
