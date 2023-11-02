package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;

public interface CommandListener<T extends ZWaveSupportedCommand> {

    CommandFilter getCommandFilter();
    void handleCommand(T command);
}
