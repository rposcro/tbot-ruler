package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.CommandType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteRegistry {

    private String registryKey(CommandType commandType, int sourceNodeId, int sourceEndpointId) {
        return commandType.name() + "-" + sourceNodeId + "-" + sourceEndpointId;
    }

    private String registryKey(CommandType commandType, int sourceNodeId) {
        return commandType.name() + "-" + sourceNodeId;
    }
}
