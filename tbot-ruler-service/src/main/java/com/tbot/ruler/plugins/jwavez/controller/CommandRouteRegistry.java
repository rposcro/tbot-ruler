package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.exceptions.PluginException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandRouteRegistry {

    private Map<String, CommandListener<ZWaveSupportedCommand>> listenersPerKey;
    private Map<CommandType, List<CommandListener<ZWaveSupportedCommand>>> listenersMap;

    public CommandRouteRegistry() {
        this.listenersMap = new HashMap<>();
        this.listenersPerKey = new HashMap<>();
    }

    public void registerListener(CommandListener<? extends ZWaveSupportedCommand> listener) {
        CommandType commandType = listener.getCommandType();
        String listenerKey = listener.getListenerKey();

        if (listenersPerKey.containsKey(listenerKey)) {
            throw new PluginException("Listener key " + listenerKey + " already registered, inconsistency exception!");
        }

        listenersPerKey.put(listenerKey, (CommandListener<ZWaveSupportedCommand>) listener);
        listenersMap.computeIfAbsent(commandType, commandType1 -> new LinkedList<>()).add((CommandListener<ZWaveSupportedCommand>) listener);
        log.info("Plugin Jwz: Registered command type {} listener {} ", commandType, listener.getClass());
    }

    public void unregisterListener(String listenerKey) {
        if (listenersPerKey.containsKey(listenerKey)) {
            CommandListener<ZWaveSupportedCommand> listener = listenersPerKey.get(listenerKey);
            CommandType commandType = listener.getCommandType();
            listenersMap.remove(listenerKey);
            listenersMap.get(commandType).remove(listener);
            log.info("Plugin Jwz: Unregistered listener {} for command type {}", listenerKey, commandType);
        } else {
            log.warn("Plugin Jwz: No listener key {} in the registry", listenerKey);
        }
    }

    public List<CommandListener<ZWaveSupportedCommand>> findListeners(CommandType commandType) {
        return listenersMap.getOrDefault(commandType, Collections.emptyList());
    }
}
