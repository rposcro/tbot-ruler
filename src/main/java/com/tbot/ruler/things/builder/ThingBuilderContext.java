package com.tbot.ruler.things.builder;

import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.things.service.ServiceProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingBuilderContext {

    private ServiceProvider services;
    private MessagePublisher messagePublisher;
    private ThingPluginDTO pluginDTO;
    private ThingDTO thingDTO;
}
