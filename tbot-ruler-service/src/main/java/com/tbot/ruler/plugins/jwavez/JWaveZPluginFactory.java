package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class JWaveZPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return JWaveZPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }

}
