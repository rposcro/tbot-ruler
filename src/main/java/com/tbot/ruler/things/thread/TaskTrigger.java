package com.tbot.ruler.things.thread;

import java.util.Date;

public interface TaskTrigger {

    Date nextEmissionTime(EmissionTriggerContext context);
}
