package com.tbot.ruler.plugins.email;

import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.subjects.plugin.Plugin;

public class EmailPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return EmailPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
