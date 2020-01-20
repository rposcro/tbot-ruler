package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.things.thread.TaskTrigger;
import com.tbot.ruler.things.thread.EmissionTriggerContext;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Builder
public class SunEventTrigger implements TaskTrigger {

    private ZoneId zoneId;
    private SunEventTimer timer;
    private long plusMinutes;

    @Override
    public Date nextEmissionTime(EmissionTriggerContext context) {
        LocalDate today = ZonedDateTime.now(zoneId).toLocalDate();
        Instant eventTimeToday = timer.forDate(today).plusMinutes(plusMinutes).toInstant();
        Date nextEmissionTime;

        if (context.getLastScheduledExecutionTime() == null) {
            nextEmissionTime = new Date(eventTimeToday.toEpochMilli());
        }
        else {
            Instant lastExecutionTime = context.getLastScheduledExecutionTime().toInstant();
            if (lastExecutionTime.isBefore(eventTimeToday)) {
                nextEmissionTime = new Date(eventTimeToday.toEpochMilli());
            }
            else {
                nextEmissionTime = new Date(timer.forDate(today.plusDays(1)).toInstant().toEpochMilli());
            }
        }

        log.debug("Next emission time for sun event is {}", nextEmissionTime);
        return nextEmissionTime;
    }
}
