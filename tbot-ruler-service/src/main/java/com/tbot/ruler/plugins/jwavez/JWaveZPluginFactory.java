package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.RulerPluginContext;

public class JWaveZPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return JWaveZPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }

}
