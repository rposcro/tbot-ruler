package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class SunWatchPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return SunWatchPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
