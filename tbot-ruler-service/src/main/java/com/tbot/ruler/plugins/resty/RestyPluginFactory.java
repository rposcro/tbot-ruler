package com.tbot.ruler.plugins.resty;

import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class RestyPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return RestyPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
