package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.task.TaskTrigger;
import com.tbot.ruler.task.EmissionTriggerContext;
import org.springframework.scheduling.support.CronExpression;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import static com.rposcro.jwavez.core.utils.ObjectsUtil.orDefault;

public class CronEmissionTrigger implements TaskTrigger {

    private final CronExpression cronExpression;
    private final ZoneId zoneId;

    public CronEmissionTrigger(String pattern, TimeZone timeZone) {
        this.cronExpression = CronExpression.parse(pattern);
        this.zoneId = timeZone.toZoneId();
    }

    @Override
    public Date nextEmissionTime(EmissionTriggerContext context) {
        Date lastExecutionTime = orDefault(context.getLastScheduledExecutionTime(), () -> new Date());
        ZonedDateTime lastExecutionDateTime = ZonedDateTime.ofInstant(lastExecutionTime.toInstant(), zoneId);
        return new Date(cronExpression.next(lastExecutionDateTime).toEpochSecond() * 1000);
    }
}
