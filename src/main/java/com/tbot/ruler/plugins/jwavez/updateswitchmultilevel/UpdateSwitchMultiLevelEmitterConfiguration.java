package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSwitchMultiLevelEmitterConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(defaultValue = "0")
    private int pollStateInterval;
}
