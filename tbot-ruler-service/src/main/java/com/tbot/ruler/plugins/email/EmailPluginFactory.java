package com.tbot.ruler.plugins.email;

import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.Plugin;

public class EmailPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return EmailPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
