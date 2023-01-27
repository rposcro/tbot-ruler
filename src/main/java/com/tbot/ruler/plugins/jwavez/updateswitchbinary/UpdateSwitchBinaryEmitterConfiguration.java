package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSwitchBinaryEmitterConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    @JsonProperty(defaultValue = "0")
    private int endPointId;
    @JsonProperty(defaultValue = "0")
    private int pollStateInterval;
}
