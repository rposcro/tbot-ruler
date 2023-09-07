package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class UpdateSwitchMultiLevelEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "update-switch-binary";

    private final SwitchBinaryReportListener reportHandler;
    private final JWaveZThingContext thingContext;

    public UpdateSwitchMultiLevelEmitterBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
        this.reportHandler = new SwitchBinaryReportListener(thingContext.getJwzApplicationSupport());
    }

    @Override
    public CommandType getSupportedCommandType() {
        return SwitchBinaryCommandType.BINARY_SWITCH_REPORT;
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
    public Emitter buildEmitter(EmitterDTO emitterDTO) throws PluginException {
        try {
            UpdateSwitchBinaryEmitterConfiguration configuration = new ObjectMapper().readerFor(UpdateSwitchBinaryEmitterConfiguration.class)
                    .readValue(emitterDTO.getConfigurationNode());
            UpdateSwitchBinaryEmitter emitter = UpdateSwitchBinaryEmitter.builder()
                    .id(emitterDTO.getId())
                    .name(emitterDTO.getName())
                    .description(emitterDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .messagePublisher(thingContext.getMessagePublisher())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
            reportHandler.registerEmitter(configuration.getNodeId(), configuration.getEndPointId(), emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse emitter's configuration!", e);
        }
    }
}
