package com.tbot.ruler.plugins.ghost.singleactivator;

import com.tbot.ruler.plugins.ghost.ActuatorBuilder;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.things.thread.RegularEmissionTrigger;

public class SingleIntervalActuatorBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "single-interval";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        return BasicActuator.builder()
                .taskTrigger(new RegularEmissionTrigger(600_000))
                .build();
    }
}
