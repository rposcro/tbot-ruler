package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.BasicPlugin;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Thing;

public class SunWatchPluginBuilder implements PluginBuilder {

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .build();
    }


    private Thing buildThing(ThingEntity thingEntity) {
        return BasicThing.builder()
                .build();
    }
}
