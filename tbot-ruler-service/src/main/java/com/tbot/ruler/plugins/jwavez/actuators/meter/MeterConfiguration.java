package com.tbot.ruler.plugins.jwavez.actuators.meter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeterConfiguration {

    @JsonProperty(required = true)
    private int nodeId;

    private int nodeEndPointId;

    @JsonProperty(defaultValue = "false")
    private boolean multiChannelOn;
}
