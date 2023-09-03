package com.tbot.ruler.plugins.ghost.singleactivator;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(required = true)
    private LocalTime activationTime;
    @JsonProperty(required = true)
    private LocalTime deactivationTime;
    @JsonProperty(required = false, defaultValue = "0")
    private long variationMinutes;
}
