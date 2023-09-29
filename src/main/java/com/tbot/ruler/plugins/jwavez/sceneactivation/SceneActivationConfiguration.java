package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SceneActivationConfiguration {

    @JsonProperty(required = true)
    private int sourceNodeId;
    @JsonProperty(required = true)
    private int sceneId;
}
