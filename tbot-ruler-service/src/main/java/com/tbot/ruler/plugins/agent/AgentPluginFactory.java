package com.tbot.ruler.plugins.agent;

import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class AgentPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return AgentPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
