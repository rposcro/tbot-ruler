package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class UpdateColorBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "update-color";

    private final JWaveZThingContext thingContext;
    private final SwitchColorReportListener reportHandler = new SwitchColorReportListener();

    public UpdateColorBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public CommandType getSupportedCommandType() {
        return SwitchColorCommandType.SWITCH_COLOR_REPORT;
    }

    @Override
    public JWaveZCommandListener<? extends ZWaveSupportedCommand> getSupportedCommandHandler() {
        return this.reportHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        try {
            UpdateColorConfiguration configuration = new ObjectMapper().readerFor(UpdateColorConfiguration.class)
                    .readValue(actuatorDTO.getConfigurationNode());
            UpdateColorActuator emitter = UpdateColorActuator.builder()
                    .id(actuatorDTO.getUuid())
                    .name(actuatorDTO.getName())
                    .description(actuatorDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .messagePublisher(thingContext.getMessagePublisher())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
            reportHandler.registerEmitter(emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse actuator's configuration!", e);
        }
    }
}
