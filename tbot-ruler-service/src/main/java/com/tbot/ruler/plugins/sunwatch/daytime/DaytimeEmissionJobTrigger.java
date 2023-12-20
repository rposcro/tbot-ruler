package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.jobs.JobTriggerContext;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DaytimeEmissionJobTrigger implements JobTrigger {

    private SunCalculator sunCalculator;
    private long emissionIntervalMinutes;
    private long lastEmissionTime;

    @Builder
    public DaytimeEmissionJobTrigger(
            @NonNull SunCalculator sunCalculator,
            long emissionIntervalMinutes) {
        this.sunCalculator = sunCalculator;
        this.emissionIntervalMinutes = emissionIntervalMinutes;
    }

    @Override
    public long nextDoJobTime(JobTriggerContext context) {
        long nextEmissionTime;

        if (lastEmissionTime == 0) {
            nextEmissionTime = System.currentTimeMillis();
        } else if (emissionIntervalMinutes > 0) {
            nextEmissionTime = System.currentTimeMillis() + (emissionIntervalMinutes * 60_000);
        } else if (sunCalculator.isDaytimeNow()) {
            nextEmissionTime = sunCalculator.nextSunset().toInstant().toEpochMilli();
        } else {
            nextEmissionTime = sunCalculator.nextSunrise().toInstant().toEpochMilli();
        }

        lastEmissionTime = nextEmissionTime;
        return nextEmissionTime;
    }
}
