package com.tbot.ruler.plugins;

import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.subjects.Plugin;

public interface PluginBuilder {

    Plugin buildPlugin(PluginEntity pluginEntity);
}
