package com.tbot.ruler.plugins.jwavez;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWaveZPluginConfiguration {

    @JsonProperty(required = true)
    private String moduleDevice;
    @JsonProperty(defaultValue = "5")
    private int reconnectAttempts;
    @JsonProperty(defaultValue = "36")
    private int reconnectDelay;
}
