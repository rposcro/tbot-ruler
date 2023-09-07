package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public interface EmitterBuilder {

    CommandType getSupportedCommandType();
    JWaveZCommandListener<? extends ZWaveSupportedCommand> getSupportedCommandHandler();
    String getReference();
    Emitter buildEmitter(EmitterDTO emitterDTO) throws PluginException;
}
