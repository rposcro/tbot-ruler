package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class UpdateColorEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "update-color";

    private final JWaveZThingContext thingContext;
    private final SwitchColorReportListener reportHandler = new SwitchColorReportListener();

    public UpdateColorEmitterBuilder(JWaveZThingContext thingContext) {
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
    public Emitter buildEmitter(EmitterDTO emitterDTO) throws PluginException {
        try {
            UpdateColorEmitterConfiguration configuration = new ObjectMapper().readerFor(UpdateColorEmitterConfiguration.class)
                    .readValue(emitterDTO.getConfigurationNode());
            UpdateColorEmitter emitter = UpdateColorEmitter.builder()
                    .id(emitterDTO.getId())
                    .name(emitterDTO.getName())
                    .description(emitterDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .messagePublisher(thingContext.getMessagePublisher())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
            reportHandler.registerEmitter(emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse emitter's configuration!", e);
        }
    }
}
