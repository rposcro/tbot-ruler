package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class UpdateSwitchMultiLevelBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "update-switch-multilevel";

    private final JWaveZThingContext thingContext;
    private final SwitchMultiLevelReportListener reportHandler = new SwitchMultiLevelReportListener();

    public UpdateSwitchMultiLevelBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public CommandType getSupportedCommandType() {
        return SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT;
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
            UpdateSwitchMultiLevelConfiguration configuration = new ObjectMapper().readerFor(UpdateSwitchMultiLevelConfiguration.class)
                    .readValue(actuatorDTO.getConfigurationNode());
            UpdateSwitchMultiLevelActuator actuator = UpdateSwitchMultiLevelActuator.builder()
                    .id(actuatorDTO.getId())
                    .name(actuatorDTO.getName())
                    .description(actuatorDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .messagePublisher(thingContext.getMessagePublisher())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
            reportHandler.registerEmitter(actuator);
            return actuator;
        } catch (IOException e) {
            throw new PluginException("Could not parse actuator's configuration!", e);
        }
    }
}
