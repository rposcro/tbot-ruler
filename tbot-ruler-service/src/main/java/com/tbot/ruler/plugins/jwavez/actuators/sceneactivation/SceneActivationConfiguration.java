package com.tbot.ruler.plugins.jwavez.actuators.sceneactivation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SceneActivationConfiguration {

    @JsonProperty(required = true)
    private int nodeId;

    @JsonProperty(required = true)
    private int sceneId;
}
