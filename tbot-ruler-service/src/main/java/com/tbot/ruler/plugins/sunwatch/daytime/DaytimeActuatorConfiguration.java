package com.tbot.ruler.plugins.sunwatch.daytime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaytimeActuatorConfiguration {

    @JsonProperty(defaultValue = "5")
    private long emissionInterval;

    // In minutes
    @JsonProperty(defaultValue = "0")
    private long sunriseShift;

    // In minutes
    @JsonProperty(defaultValue = "0")
    private long sunsetShift;

    @JsonProperty(defaultValue = "on")
    private String dayTimeSignal;

    @JsonProperty(defaultValue = "off")
    private String nightTimeSignal;
}
