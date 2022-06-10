package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchBinaryConfiguration {

    @JsonProperty(required = true)
    private int nodeId;
    private int destinationEndPointId;
    private boolean multiChannelOn;
}
