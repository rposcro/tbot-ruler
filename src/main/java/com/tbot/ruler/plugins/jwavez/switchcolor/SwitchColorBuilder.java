package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SwitchColorBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "switch-color";

    private final JWaveZThingContext thingContext;

    public SwitchColorBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        try {
            SwitchColorConfiguration configuration = new ObjectMapper().readerFor(SwitchColorConfiguration.class)
                    .readValue(actuatorDTO.getConfigurationNode());
            return SwitchColorActuator.builder()
                    .id(actuatorDTO.getUuid())
                    .name(actuatorDTO.getName())
                    .description(actuatorDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
        } catch (IOException e) {
            throw new PluginException("Could not parse collector's configuration!", e);
        }
    }

    @Override
    public CommandType getSupportedCommandType() {
        return null;
    }

    @Override
    public JWaveZCommandListener<? extends ZWaveSupportedCommand> getSupportedCommandHandler() {
        return null;
    }
}
