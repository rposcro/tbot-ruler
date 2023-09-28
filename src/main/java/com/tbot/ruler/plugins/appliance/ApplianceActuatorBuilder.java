package com.tbot.ruler.plugins.appliance;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.Actuator;
import lombok.Getter;

public abstract class ApplianceActuatorBuilder {

    @Getter
    protected String reference;

    protected ApplianceActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(ActuatorEntity actuatorEntity, MessagePublisher messagePublisher);
}
