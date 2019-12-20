package com.tbot.ruler.things.builder;

import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.exceptions.PluginException;

public interface ThingPluginBuilder {

    Thing buildThing(ThingBuilderContext builderContext) throws PluginException;
}
