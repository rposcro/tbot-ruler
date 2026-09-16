package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import lombok.Getter;

import java.util.Set;

@Getter
public class CentralScenePressConfiguration {

    @JsonProperty(required = true)
    private int nodeId;

    @JsonProperty(required = true)
    private int sceneId;

    @JsonProperty(required = true)
    private CentralSceneKeyAttribute keyAttribute;
}
