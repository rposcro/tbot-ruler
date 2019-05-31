package com.tbot.ruler.things;

import com.tbot.ruler.things.exceptions.PluginException;

public interface ThingPluginBuilder {

    Thing buildThing(ThingBuilderContext builderContext) throws PluginException;
}
