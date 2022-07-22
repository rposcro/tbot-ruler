package com.tbot.ruler.plugins.jwavez.basicset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicSetEmitterConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    private int sourceEndPointId;
    @JsonProperty(defaultValue = "false")
    private boolean multiChannelOn;

    @JsonProperty(defaultValue = "toggle")
    private String valueMode;
    @JsonProperty(defaultValue = "255")
    private int turnOnValue;
    @JsonProperty(defaultValue = "0")
    private int turnOffValue;
}
