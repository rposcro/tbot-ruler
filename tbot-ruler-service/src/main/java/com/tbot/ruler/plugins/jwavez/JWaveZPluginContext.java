package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandRouteRegistry;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.plugins.jwavez.controller.SerialController;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@NonNull
public class JWaveZPluginContext {

    private final RulerPluginContext rulerPluginContext;
    private final SerialController serialController;
    private final JwzApplicationSupport jwzApplicationSupport;
    private final CommandSender jwzCommandSender;
    private final CommandRouteRegistry commandRouteRegistry;
}
