package com.tbot.ruler.plugins.cron;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CronActuatorConfiguration {

    @JsonProperty(required = true)
    private String switchOnSchedule;

    @JsonProperty(required = true)
    private String switchOffSchedule;

    @JsonProperty(defaultValue = "off")
    private String defaultState;

    @JsonProperty(defaultValue = "5")
    private long emissionInterval;

    @JsonProperty(required = true)
    private String timeZone;
}
