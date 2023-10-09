package com.tbot.ruler.plugins.ghost.singleinterval;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tbot.ruler.util.jackson.StringToLocalTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleIntervalConfiguration {

    @JsonProperty(defaultValue = "true")
    private boolean enabledByDefault;

    @JsonProperty(required = true)
    @JsonDeserialize(converter = StringToLocalTimeConverter.class)
    private LocalTime activationTime;

    @JsonProperty(required = true)
    @JsonDeserialize(converter = StringToLocalTimeConverter.class)
    private LocalTime deactivationTime;

    @JsonProperty(defaultValue = "0")
    private long variationMinutes;

    @JsonProperty(defaultValue = "5")
    private long emissionIntervalMinutes;
}
