package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SwitchBinaryBuilder implements ActuatorBuilder {

    private final static String REFERENCE = "switch-binary";

    private final JWaveZThingContext thingContext;

    public SwitchBinaryBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SwitchBinaryActuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        try {
            SwitchBinaryConfiguration configuration = new ObjectMapper().readerFor(SwitchBinaryConfiguration.class).readValue(actuatorDTO.getConfigurationNode());

            return SwitchBinaryActuator.builder()
                    .id(actuatorDTO.getId())
                    .name(actuatorDTO.getName())
                    .description(actuatorDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
        } catch(IOException e) {
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
