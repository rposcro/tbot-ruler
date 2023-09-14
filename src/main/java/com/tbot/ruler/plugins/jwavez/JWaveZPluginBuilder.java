package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.plugins.BasicPlugin;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;

public class JWaveZPluginBuilder implements PluginBuilder {

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .build();
    }

}
