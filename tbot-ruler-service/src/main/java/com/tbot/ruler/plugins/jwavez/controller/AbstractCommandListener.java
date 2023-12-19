package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandListener<T extends ZWaveSupportedCommand> implements CommandListener<T> {

    private CommandType commandType;
    private String listenerKey;
}
