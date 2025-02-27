package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CentralSceneHoldConfiguration {

    @JsonProperty(required = true)
    private int nodeId;

    @JsonProperty(required = true)
    private int sceneId;

    @JsonProperty(defaultValue = "2000")
    private long minMillisecondsOfHold;

    @JsonProperty(defaultValue = "4000")
    private long maxMillisecondsOfHold;
}
