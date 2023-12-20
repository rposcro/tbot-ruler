package com.tbot.ruler.plugins.sunwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SunWatchPluginConfiguration {

    @JsonProperty(required = true)
    private String latitude;
    @JsonProperty(required = true)
    private String longitude;
    @JsonProperty(required = true)
    private String timeZone;
}
