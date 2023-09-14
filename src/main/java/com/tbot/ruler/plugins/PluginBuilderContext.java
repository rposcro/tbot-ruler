package com.tbot.ruler.plugins;

import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.things.service.ServiceProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PluginBuilderContext {

    private ServiceProvider serviceProvider;
    private MessagePublisher messagePublisher;
    private PluginEntity pluginEntity;
}
