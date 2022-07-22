package com.tbot.ruler.plugins.sunwatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SunEventEmitterConfiguration {

    @JsonProperty(defaultValue = "0")
    private long shift;
    @JsonProperty(required = true)
    private String signal;
}
