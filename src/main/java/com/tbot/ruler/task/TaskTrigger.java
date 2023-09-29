package com.tbot.ruler.task;

import java.util.Date;

public interface TaskTrigger {

    Date nextEmissionTime(EmissionTriggerContext context);
}
