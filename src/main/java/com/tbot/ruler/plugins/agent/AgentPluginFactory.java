package com.tbot.ruler.plugins.agent;

import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.RulerPluginContext;

public class AgentPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return AgentPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
