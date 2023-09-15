package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class _JWaveZThingContext {

    private final ThingBuilderContext builderContext;
    private final JwzApplicationSupport jwzApplicationSupport;
    private final JWaveZCommandSender jwzCommandSender;
    private final MessagePublisher messagePublisher;
}
