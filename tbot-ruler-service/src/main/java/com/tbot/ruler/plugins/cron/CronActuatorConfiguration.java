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
    private String schedulePattern;

    @JsonProperty(required = true)
    private String timeZone;
}
