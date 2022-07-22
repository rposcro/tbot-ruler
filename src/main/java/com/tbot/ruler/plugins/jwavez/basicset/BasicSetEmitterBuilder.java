package com.tbot.ruler.plugins.jwavez.basicset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class BasicSetEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "basic-set";

    private BasicSetCommandHandler basicSetCommandHandler = new BasicSetCommandHandler();

    @Override
    public CommandType getSupportedCommandType() {
        return BasicCommandType.BASIC_SET;
    }

    @Override
    public JWaveZCommandHandler<BasicSet> getSupportedCommandHandler() {
        return basicSetCommandHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Emitter buildEmitter(JWaveZAgent agent, ThingBuilderContext builderContext, EmitterDTO emitterDTO) throws PluginException {
        try {
            BasicSetEmitterConfiguration configuration = new ObjectMapper().readerFor(BasicSetEmitterConfiguration.class).readValue(emitterDTO.getConfigurationNode());
            BasicSetEmitter emitter = BasicSetEmitter.builder()
                    .id(emitterDTO.getId())
                    .name(emitterDTO.getName())
                    .description(emitterDTO.getDescription())
                    .configuration(configuration)
                    .messagePublisher(builderContext.getMessagePublisher())
                    .build();
            basicSetCommandHandler.registerEmitter(emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse emitter's configuration!", e);
        }
    }
}
