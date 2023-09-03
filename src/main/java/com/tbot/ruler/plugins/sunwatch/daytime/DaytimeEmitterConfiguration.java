package com.tbot.ruler.plugins.sunwatch.daytime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaytimeEmitterConfiguration {

    @JsonProperty(defaultValue = "0")
    private long emissionInterval;
    @JsonProperty(defaultValue = "0")
    private long sunriseShift;
    @JsonProperty(defaultValue = "0")
    private long sunsetShift;
    @JsonProperty(defaultValue = "on")
    private String dayTimeSignal;
    @JsonProperty(defaultValue = "off")
    private String nightTimeSignal;
}
