package com.tbot.ruler.plugins.agent.signaler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignalerActuatorConfiguration {

    @JsonProperty(required = true)
    private String signalType;

    @JsonProperty(required = true)
    private JsonNode signalValue;
}
