package com.tbot.ruler.it;

import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PluginsHelper {

    @Autowired
    private PluginsRepository pluginsRepository;

    public PluginEntity insertPlugin() {
        PluginEntity pluginEntity = newPluginEntity();
        return pluginsRepository.save(pluginEntity);
    }

    public PluginEntity newPluginEntity() {
        return PluginEntity.builder()
                .pluginUuid("plgn-" + UUID.randomUUID())
                .factoryClass("com.tbot.ruler.plugins.TestPlugin")
                .name("Plugin Name")
                .configuration(new TextNode("plugin-config"))
                .build();
    }
}
