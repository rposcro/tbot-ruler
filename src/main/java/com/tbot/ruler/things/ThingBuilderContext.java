package com.tbot.ruler.things;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.things.dto.ThingDTO;
import com.tbot.ruler.things.dto.ThingPluginDTO;
import com.tbot.ruler.things.service.ServiceProvider;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

@Builder
@Getter
public class ThingBuilderContext {
    private ServiceProvider services;
    private Consumer<EmitterSignal> signalConsumer;
    private ThingPluginDTO pluginDTO;
    private ThingDTO thingDTO;
}
