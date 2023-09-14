package com.tbot.ruler.plugins;

import com.tbot.ruler.persistance.model.PluginEntity;

public interface PluginBuilder {

    Plugin buildPlugin(PluginBuilderContext builderContext);
}
