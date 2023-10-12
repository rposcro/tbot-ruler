package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.subjects.service.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class PluginServiceFactory {

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
                    log.info("Building plugin: {} {} {}", pluginEntity.getPluginUuid(), pluginEntity.getName(), pluginEntity.getFactoryClass());
                    Plugin plugin = buildPlugin(pluginEntity);
                    plugins.add(plugin);
                } catch(ReflectiveOperationException e) {
                    log.error("Failed to instantiate plugin builder!", e);
                }
            });
        return plugins;
    }

    public Plugin buildPlugin(PluginEntity pluginEntity) throws ReflectiveOperationException {
        RulerPluginContext context = RulerPluginContext.builder()
                .pluginUuid(pluginEntity.getPluginUuid())
                .pluginName(pluginEntity.getName())
                .pluginConfiguration(pluginEntity.getConfiguration())
                .serviceProvider(serviceProvider)
                .messagePublisher(messagePublisher)
                .subjectStateService(subjectStateService)
                .build();
        PluginFactory factory = instantiateFactory(pluginEntity);
        return factory.producePlugin(context);
    }

    private PluginFactory instantiateFactory(PluginEntity pluginEntity) throws ReflectiveOperationException {
        String builderClassName = pluginEntity.getFactoryClass();
        Class<?> builderClass = this.getClass().getClassLoader().loadClass(builderClassName);
        return (PluginFactory) builderClass.getConstructor().newInstance();
    }
}
