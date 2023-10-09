package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.service.things.SubjectStateService;
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
    @Autowired
    private SubjectStateService subjectStateService;

    public List<Plugin> buildPlugins(Iterable<PluginEntity> pluginEntities) {
        List<Plugin> plugins = new LinkedList<>();
        pluginEntities.forEach(pluginEntity -> {
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
                .serviceProvider(serviceProvider)
                .messagePublisher(messagePublisher)
                .subjectStateService(subjectStateService)
                .build();
        PluginBuilder builder = instantiateBuilder(pluginEntity, context);
        return builder.buildPlugin(pluginEntity);
    }

    private PluginBuilder instantiateBuilder(PluginEntity pluginEntity, PluginBuilderContext pluginBuilderContext) throws ReflectiveOperationException {
        String builderClassName = pluginEntity.getBuilderClass();
        Class<?> builderClass = this.getClass().getClassLoader().loadClass(builderClassName);
        return (PluginBuilder) builderClass.getConstructor(PluginBuilderContext.class).newInstance(pluginBuilderContext);
    }

}
