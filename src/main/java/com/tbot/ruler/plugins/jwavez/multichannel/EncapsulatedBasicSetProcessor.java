package com.tbot.ruler.plugins.jwavez.multichannel;

import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;

public class EncapsulatedBasicSetProcessor implements EncapsulatedCommandProcessor {

    @Override
    public void handle(MultiChannelCommandEncapsulation commandEncapsulation) {
        byte nodeId = commandEncapsulation.getSourceNodeId().getId();
        byte
    }
}
