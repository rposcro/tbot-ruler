package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public interface GhostActuatorBuilder {

    String getReference();
    Actuator buildActuator(ActuatorDTO actuatorDTO, MessagePublisher messagePublisher, GhostThingConfiguration ghostThingConfiguration) throws PluginException;
}
