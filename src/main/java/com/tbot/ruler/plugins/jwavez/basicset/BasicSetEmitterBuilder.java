package com.tbot.ruler.plugins.jwavez.basicset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class BasicSetEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "basic-set";

    private final JWaveZThingContext thingContext;
    private final BasicSetCommandListener basicSetCommandHandler;

    public BasicSetEmitterBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
        basicSetCommandHandler = new BasicSetCommandListener(thingContext.getJwzApplicationSupport().supportedCommandParser());
    }

    @Override
    public CommandType getSupportedCommandType() {
        return BasicCommandType.BASIC_SET;
    }

    @Override
    public JWaveZCommandListener<BasicSet> getSupportedCommandHandler() {
        return basicSetCommandHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Emitter buildEmitter(EmitterDTO emitterDTO) throws PluginException {
        try {
            BasicSetEmitterConfiguration configuration = new ObjectMapper().readerFor(BasicSetEmitterConfiguration.class).readValue(emitterDTO.getConfigurationNode());
            BasicSetEmitter emitter = BasicSetEmitter.builder()
                    .id(emitterDTO.getId())
                    .name(emitterDTO.getName())
                    .description(emitterDTO.getDescription())
                    .configuration(configuration)
                    .messagePublisher(thingContext.getMessagePublisher())
                    .build();
            basicSetCommandHandler.registerEmitter(emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse emitter's configuration!", e);
        }
    }
}
