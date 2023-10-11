package com.tbot.ruler.task;

import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
public class RegularEmissionTrigger implements TaskTrigger {

    private long periodMilliseconds;

    @Override
    public Date nextEmissionTime(EmissionTriggerContext context) {
        Date lastExecutionTime = context.getLastScheduledExecutionTime();
        return new Date(Optional.ofNullable(lastExecutionTime).orElse(new Date()).getTime() + periodMilliseconds);
    }
}
