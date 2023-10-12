package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.RulerPluginContext;

public class SunWatchPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return SunWatchPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
