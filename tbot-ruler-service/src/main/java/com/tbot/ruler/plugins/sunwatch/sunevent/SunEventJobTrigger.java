package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.jobs.JobTriggerContext;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Builder
public class SunEventJobTrigger implements JobTrigger {

    private ZoneId zoneId;
    private SunEventTimer timer;
    private long plusMinutes;

    @Override
    public long nextDoJobTime(JobTriggerContext context) {
        LocalDate today = ZonedDateTime.now(zoneId).toLocalDate();
        long eventTimeToday = timer.forDate(today).plusMinutes(plusMinutes).toInstant().toEpochMilli();
        long nextEmissionTime;

        if (context.isFirstRun()) {
            nextEmissionTime = eventTimeToday;
        }
        else {
            if (context.getLastScheduledExecutionTime() < eventTimeToday) {
                nextEmissionTime = eventTimeToday;
            }
            else {
                nextEmissionTime = timer.forDate(today.plusDays(1)).toInstant().toEpochMilli();
            }
        }

        return nextEmissionTime;
    }
}
