package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class SingleIntervalActuator extends AbstractActuator {

    private final SingleIntervalAgent singleIntervalAgent;

    @Builder
    public SingleIntervalActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull SingleIntervalAgent singleIntervalAgent,
            @NonNull @Singular Collection<JobBundle> jobBundles
    ) {
        super(uuid, name, description, jobBundles);
        this.singleIntervalAgent = singleIntervalAgent;
    }

    @Override
    public ActuatorState getState() {
        return singleIntervalAgent.getCurrentState();
    }
}
