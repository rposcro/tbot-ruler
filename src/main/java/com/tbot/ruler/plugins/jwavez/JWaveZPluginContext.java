package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.plugins.PluginBuilderContext;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWaveZPluginContext {

    private final PluginBuilderContext pluginBuilderContext;
    private final MessagePublisher messagePublisher;
    private final JwzApplicationSupport jwzApplicationSupport;
    private final JWaveZSerialController jwzSerialController;
    private final JWaveZSerialHandler jwzSerialHandler;
    private final JWaveZCommandSender jwzCommandSender;
}
