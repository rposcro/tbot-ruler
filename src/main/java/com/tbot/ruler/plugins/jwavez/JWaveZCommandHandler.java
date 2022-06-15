package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JWaveZCommandHandler<T extends ZWaveSupportedCommand> implements SupportedCommandHandler<T> {

    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.info("Ignored command encapsulation from node: "
                + commandEncapsulation.getSourceNodeId() + "/" + commandEncapsulation.getSourceEndpointId());
    }
}
