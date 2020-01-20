package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.things.thread.TaskTrigger;
import com.tbot.ruler.things.thread.EmissionTriggerContext;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Builder
public class DaytimeEmissionTrigger implements TaskTrigger {

    private SunCalculator sunCalculator;
    private ZoneId zoneId;
    private long plusSunriseMinutes;
    private long plusSunsetMinutes;
    @Builder.Default
    private Optional<Boolean> lastTriggerOnSunrise = Optional.empty();

    boolean triggeredOnSunrise() {
        return lastTriggerOnSunrise.get();
    }

    @Override
    public Date nextEmissionTime(EmissionTriggerContext context) {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        Date nextEmissionTime;

        if (!lastTriggerOnSunrise.isPresent()) {
            lastTriggerOnSunrise = Optional.of(sunCalculator.isDaytimeAt(now, plusSunriseMinutes, plusSunsetMinutes));
            nextEmissionTime = new Date();
        }
        else if (lastTriggerOnSunrise.get()) {
            lastTriggerOnSunrise = Optional.of(Boolean.FALSE);
            nextEmissionTime = new Date(sunCalculator.sunsetForDate(today).plusMinutes(plusSunsetMinutes).toInstant().toEpochMilli());
        }
        else {
            lastTriggerOnSunrise = Optional.of(Boolean.TRUE);
            nextEmissionTime = new Date(sunCalculator.sunriseForDate(today.plusDays(1)).plusMinutes(plusSunriseMinutes).toInstant().toEpochMilli());
        }

        log.debug("Next emission time for sun event is {}", nextEmissionTime);
        return nextEmissionTime;
    }
}
