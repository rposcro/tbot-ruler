package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import lombok.Getter;

@Getter
public class CentralSceneHoldConfiguration {

    @JsonProperty(required = true)
    private int nodeId;

    @JsonProperty(required = true)
    private int sceneId;

    @JsonProperty(defaultValue = "500")
    private long millisecondsOfHold;
}
