package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public interface _ActuatorBuilder {

    CommandType getSupportedCommandType();
    JWaveZCommandListener<? extends ZWaveSupportedCommand> getSupportedCommandHandler();
    String getReference();
    Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException;
}
