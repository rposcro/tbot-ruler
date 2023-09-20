package com.tbot.ruler.threads;

import java.util.Date;

public interface TaskTrigger {

    Date nextEmissionTime(EmissionTriggerContext context);
}
