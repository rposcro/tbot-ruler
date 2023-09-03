package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.things.thread.TaskTrigger;
import com.tbot.ruler.things.thread.EmissionTriggerContext;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class DaytimeEmissionTrigger implements TaskTrigger {

    private SunCalculator sunCalculator;
    private ZoneId zoneId;
    private long emissionIntervalMinutes;

    private Date lastEmissionTime;

    @Builder
    public DaytimeEmissionTrigger(
            @NonNull SunCalculator sunCalculator,
            @NonNull ZoneId zoneId,
            long emissionIntervalMinutes) {
        this.sunCalculator = sunCalculator;
        this.zoneId = zoneId;
        this.emissionIntervalMinutes = emissionIntervalMinutes;
    }

    @Override
    public Date nextEmissionTime(EmissionTriggerContext context) {
        Date nextEmissionTime;

        if (lastEmissionTime == null) {
            nextEmissionTime = new Date();
        } else if (emissionIntervalMinutes > 0) {
            nextEmissionTime = new Date(System.currentTimeMillis() + (emissionIntervalMinutes * 60_000));
        } else if (sunCalculator.isDaytimeNow()) {
            nextEmissionTime = new Date(sunCalculator.nextSunset().toInstant().toEpochMilli());
        } else {
            nextEmissionTime = new Date(sunCalculator.nextSunrise().toInstant().toEpochMilli());
        }

        log.debug("Next emission time for sun event is {}", nextEmissionTime);
        lastEmissionTime = nextEmissionTime;
        return nextEmissionTime;
    }
}
