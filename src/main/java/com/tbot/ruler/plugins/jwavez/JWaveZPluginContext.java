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
    private final JwzApplicationSupport jwzApplicationSupport;
    private final JWaveZCommandSender jwzCommandSender;
    private final MessagePublisher messagePublisher;
}
