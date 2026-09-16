package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class CronPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return CronPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}

