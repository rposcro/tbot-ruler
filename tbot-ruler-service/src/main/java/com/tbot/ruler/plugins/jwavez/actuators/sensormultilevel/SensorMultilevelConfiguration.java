package com.tbot.ruler.plugins.jwavez.actuators.sensormultilevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SensorMultilevelConfiguration {

    @JsonProperty(required = true)
    private int nodeId;

    private int nodeEndPointId;

    @JsonProperty(defaultValue = "false")
    private boolean multiChannelOn;

    private String measureType;

    private String measureUnit;
}
