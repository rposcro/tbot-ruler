package com.tbot.ruler.things.service;

import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.EmissionTrigger;

public interface ThreadRegistrationService {
    void registerContinuousEmissionThread(EmissionThread runnable);
    void registerPeriodicEmissionThread(EmissionThread runnable, EmissionTrigger trigger);
    void registerStartUpTask(EmissionThread runnable);
}
