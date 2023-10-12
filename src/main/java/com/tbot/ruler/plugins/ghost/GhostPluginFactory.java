package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.RulerPluginContext;

public class GhostPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return GhostPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
