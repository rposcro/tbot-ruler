package com.tbot.ruler.plugins.ghost.randominterval;

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
public class RandomActuatorConfiguration {

    @JsonProperty(required = false)
    private LocalTime enableTime;
    @JsonProperty(required = false)
    private LocalTime disableTime;
    @JsonProperty(required = true)
    private long minActiveTime;
    @JsonProperty(required = true)
    private long maxActiveTime;
    @JsonProperty(required = true)
    private long minBreakTime;
    @JsonProperty(required = true)
    private long maxBreakTime;
}
