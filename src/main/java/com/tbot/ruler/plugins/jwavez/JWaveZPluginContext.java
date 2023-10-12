package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.plugins.RulerPluginContext;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWaveZPluginContext {

    private final RulerPluginContext rulerPluginContext;
    private final MessagePublisher messagePublisher;
    private final SerialController serialController;
    private final JwzApplicationSupport jwzApplicationSupport;
    private final JWaveZSerialHandler jwzSerialHandler;
    private final JWaveZCommandSender jwzCommandSender;
}
