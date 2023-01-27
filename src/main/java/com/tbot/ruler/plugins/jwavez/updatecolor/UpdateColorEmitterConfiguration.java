package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateColorEmitterConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(required = true)
    private String colorMode;
    @JsonProperty(defaultValue = "0")
    private int pollStateInterval;
}
