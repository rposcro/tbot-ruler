package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandRouter {


    public void routeCommand(ZWaveSupportedCommand zWaveSupportedCommand) {

    }

    public void routeEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.info("Handling encapsulation command with no action");
    }

}
