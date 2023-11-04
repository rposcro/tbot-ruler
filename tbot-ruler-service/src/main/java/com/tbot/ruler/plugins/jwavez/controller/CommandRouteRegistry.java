package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandRouteRegistry {

    private Map<CommandType, List<CommandListener<ZWaveSupportedCommand>>> listenersMap;

    public CommandRouteRegistry() {
        this.listenersMap = new HashMap<>();
    }

    public void registerListener(CommandType commandType, CommandListener<? extends ZWaveSupportedCommand> listener) {
        listenersMap.computeIfAbsent(commandType, commandType1 -> new LinkedList<>()).add((CommandListener<ZWaveSupportedCommand>) listener);
        log.info("Registered command type {} listener {} ", commandType, listener.getClass());
    }

    public List<CommandListener<ZWaveSupportedCommand>> findListeners(CommandType commandType) {
        return listenersMap.getOrDefault(commandType, Collections.emptyList());
    }
}
