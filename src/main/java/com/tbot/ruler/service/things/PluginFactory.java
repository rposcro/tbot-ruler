package com.tbot.ruler.service.things;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.service.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class PluginFactory {

    @Autowired
    private ServiceProvider serviceProvider;
    @Autowired
    private MessagePublisher messagePublisher;

    public List<Plugin> buildPlugins(List<PluginEntity> pluginEntities) {
        List<Plugin> plugins = new LinkedList<>();
        pluginEntities.stream()
                .forEach(pluginEntity -> {
                    try {
                        log.info("Building plugin: {} {} {}", pluginEntity.getPluginUuid(), pluginEntity.getName(), pluginEntity.getBuilderClass());
                        Plugin plugin = buildPlugin(pluginEntity);
                        plugins.add(plugin);
                    } catch(ReflectiveOperationException e) {
                        log.error("Failed to instantiate plugin builder!", e);
                    }
                });
        return plugins;
    }

    public Plugin buildPlugin(PluginEntity pluginEntity) throws ReflectiveOperationException {
        PluginBuilderContext context = PluginBuilderContext.builder()
                .pluginEntity(pluginEntity)
                .serviceProvider(serviceProvider)
                .messagePublisher(messagePublisher)
                .build();
        PluginBuilder builder = instantiateBuilder(pluginEntity);
        return builder.buildPlugin(context);
    }

    private PluginBuilder instantiateBuilder(PluginEntity pluginEntity) throws ReflectiveOperationException {
        String builderClassName = pluginEntity.getBuilderClass();
        Class<?> builderClass = this.getClass().getClassLoader().loadClass(builderClassName);
        return (PluginBuilder) builderClass.newInstance();
    }

}
