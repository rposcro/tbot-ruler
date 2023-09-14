package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.plugins.BasicPlugin;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;

public class GhostPluginBuilder implements PluginBuilder {

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .build();
    }
}
