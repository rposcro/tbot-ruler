package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface JWaveZCommandSender extends BiConsumer<NodeId, ZWaveControlledCommand> {
}
