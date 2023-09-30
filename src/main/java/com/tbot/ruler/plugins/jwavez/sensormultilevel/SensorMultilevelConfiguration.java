package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SensorMultilevelConfiguration {

    @JsonProperty(required = true)
    private int sourceNodeId;
    private int sourceEndPointId;
    @JsonProperty(defaultValue = "false")
    private boolean multiChannelOn;
    private String measureType;
    private String measureUnit;
}
