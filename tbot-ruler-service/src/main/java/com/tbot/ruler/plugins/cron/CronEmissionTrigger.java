package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.jobs.JobTriggerContext;
import org.springframework.scheduling.support.CronExpression;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class CronEmissionTrigger implements JobTrigger {

    private final CronExpression cronExpression;
    private final ZoneId zoneId;

    public CronEmissionTrigger(String pattern, TimeZone timeZone) {
        this.cronExpression = CronExpression.parse(pattern);
        this.zoneId = timeZone.toZoneId();
    }

    @Override
    public long nextDoJobTime(JobTriggerContext context) {
        Date lastExecutionTime = context.isFirstRun() ? new Date() : new Date(context.getLastScheduledExecutionTime());
        ZonedDateTime lastExecutionDateTime = ZonedDateTime.ofInstant(lastExecutionTime.toInstant(), zoneId);
        return new Date(cronExpression.next(lastExecutionDateTime).toEpochSecond() * 1000).getTime();
    }
}
