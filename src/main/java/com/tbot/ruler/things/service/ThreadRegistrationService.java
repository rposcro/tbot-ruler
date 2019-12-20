package com.tbot.ruler.things.service;

import com.tbot.ruler.things.thread.EmissionThread;
import com.tbot.ruler.things.thread.TaskTrigger;

public interface ThreadRegistrationService {

    void registerContinuousEmissionThread(EmissionThread runnable);
    void registerPeriodicEmissionThread(EmissionThread runnable, TaskTrigger trigger);
    void registerStartUpTask(EmissionThread runnable);
}
