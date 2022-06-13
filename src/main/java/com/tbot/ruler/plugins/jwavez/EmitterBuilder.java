package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public interface EmitterBuilder {

    CommandType getSupportedCommandType();
    SupportedCommandHandler<?> getSupportedCommandHandler();
    String getReference();
    Emitter buildEmitter(JWaveZAgent agent, ThingBuilderContext builderContext, EmitterDTO emitterDTO) throws PluginException;
}
