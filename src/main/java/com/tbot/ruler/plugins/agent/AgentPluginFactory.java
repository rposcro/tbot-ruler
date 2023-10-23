package com.tbot.ruler.plugins.agent;

import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.email.EmailPlugin;

public class AgentPluginFactory implements PluginFactory {

    @Override
    public Plugin producePlugin(RulerPluginContext rulerPluginContext) {
        return EmailPlugin.builder()
                .rulerPluginContext(rulerPluginContext)
                .build();
    }
}
