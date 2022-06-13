package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public interface ActuatorBuilder {

    CommandType getSupportedCommandType();
    SupportedCommandHandler<?> getSupportedCommandHandler();
    String getReference();
    Actuator buildActuator(JWaveZAgent agent, ThingBuilderContext builderContext, ActuatorDTO actuatorDTO) throws PluginException;
}
