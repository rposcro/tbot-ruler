package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.things.thread.TaskTrigger;
import com.tbot.ruler.things.thread.EmissionTriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

public class CronEmissionTrigger implements TaskTrigger {

    private CronSequenceGenerator sequenceGenerator;

    public CronEmissionTrigger(String pattern, TimeZone timeZone) {
        sequenceGenerator = new CronSequenceGenerator(pattern, timeZone);
    }

    @Override
    public Date nextEmissionTime(EmissionTriggerContext context) {
        Date lastExecutionTime = context.getLastScheduledExecutionTime();
        return sequenceGenerator.next(Optional.ofNullable(lastExecutionTime).orElse(new Date()));
    }
}
