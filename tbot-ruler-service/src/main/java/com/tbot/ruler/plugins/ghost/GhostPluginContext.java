package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.plugins.RulerPluginContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@NonNull
public class GhostPluginContext {

    private final RulerPluginContext rulerPluginContext;
    private final GhostPluginConfiguration ghostPluginConfiguration;
}
