package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.deputy.api.DeputyServiceApi;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Getter;

@Getter
public class DeputyPluginContext {

    private DeputyServiceApi deputyServiceApi;
    private RulerThingContext rulerThingContext;
    private RulerPluginContext rulerPluginContext;
}
