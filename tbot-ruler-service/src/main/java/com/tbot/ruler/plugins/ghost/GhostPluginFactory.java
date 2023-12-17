package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class GhostPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return GhostPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
