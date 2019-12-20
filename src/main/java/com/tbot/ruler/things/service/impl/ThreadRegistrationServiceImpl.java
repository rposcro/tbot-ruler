package com.tbot.ruler.things.service.impl;

import com.tbot.ruler.things.thread.EmissionThread;
import com.tbot.ruler.things.thread.TaskTrigger;
import com.tbot.ruler.things.service.ThreadRegistrationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Service("thingRegistrationService")
public class ThreadRegistrationServiceImpl implements ThreadRegistrationService {

    private List<EmissionThread> startUpTasks = new LinkedList<>();
    private List<EmissionThread> continuousThreads = new LinkedList<>();
    private Map<EmissionThread, TaskTrigger> periodicThreads = new HashMap<>();

    public void registerContinuousEmissionThread(EmissionThread emissionThread) {
        continuousThreads.add(emissionThread);
    }

    public void registerPeriodicEmissionThread(EmissionThread emissionThread, TaskTrigger trigger) {
        periodicThreads.put(emissionThread, trigger);
    }

    @Override
    public void registerStartUpTask(EmissionThread runnable) {
        startUpTasks.add(runnable);
    }
}
