package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;

public interface CommandFilter {

    boolean accepts(ZWaveSupportedCommand command);

    static CommandFilter sourceNodeFilter(int sourceNodeId) {
        return command -> !(command instanceof MultiChannelCommandEncapsulation)
                && command.getSourceNodeId().getId() == (byte) sourceNodeId;
    }

    static CommandFilter encapsulatedSourceFilter(int sourceNodeId, int sourceEndPointId) {
        return command -> (command instanceof MultiChannelCommandEncapsulation)
                && command.getSourceNodeId().getId() == (byte) sourceNodeId
                && ((MultiChannelCommandEncapsulation) command).getSourceEndPointId() == (byte) sourceEndPointId;
    }
}
