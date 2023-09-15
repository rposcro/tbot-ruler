package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.things.Actuator;
import lombok.Getter;

@Getter
public abstract class JWaveZActuatorBuilder {

    private String reference;
    private CommandType supportedCommandType;
    private JWaveZCommandListener<? extends ZWaveSupportedCommand> supportedCommandHandler;

    protected JWaveZActuatorBuilder(String reference) {
        this.reference = reference;
    }

    protected JWaveZActuatorBuilder(
            String reference,
            CommandType supportedCommandType,
            JWaveZCommandListener<? extends ZWaveSupportedCommand> supportedCommandHandler) {
        this.reference = reference;
        this.supportedCommandType = supportedCommandType;
        this.supportedCommandHandler = supportedCommandHandler;
    }

    public abstract Actuator buildActuator(ActuatorEntity actuatorEntity);
}
