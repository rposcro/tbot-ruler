package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.listeners.SupportedCommandListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JWaveZCommandListener<T extends ZWaveSupportedCommand> implements SupportedCommandListener<T> {

    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.info("Handling encapsulation command with no action");
    }
}
