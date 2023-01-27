package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class UpdateSwitchMultiLevelEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "update-switch-multilevel";

    private final SwitchMultiLevelReportHandler reportHandler = new SwitchMultiLevelReportHandler();

    @Override
    public CommandType getSupportedCommandType() {
        return SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT;
    }

    @Override
    public JWaveZCommandHandler<? extends ZWaveSupportedCommand> getSupportedCommandHandler() {
        return this.reportHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Emitter buildEmitter(JWaveZAgent agent, ThingBuilderContext builderContext, EmitterDTO emitterDTO) throws PluginException {
        try {
            UpdateSwitchMultiLevelEmitterConfiguration configuration = new ObjectMapper().readerFor(UpdateSwitchMultiLevelEmitterConfiguration.class)
                    .readValue(emitterDTO.getConfigurationNode());
            UpdateSwitchMultiLevelEmitter emitter = UpdateSwitchMultiLevelEmitter.builder()
                    .id(emitterDTO.getId())
                    .name(emitterDTO.getName())
                    .description(emitterDTO.getDescription())
                    .commandSender(agent.getCommandSender())
                    .messagePublisher(builderContext.getMessagePublisher())
                    .configuration(configuration)
                    .build();
            reportHandler.registerEmitter(emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse collector's configuration!", e);
        }
    }
}
