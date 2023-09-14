package com.tbot.ruler.configuration;

import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.service.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class PluginsConfiguration {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private MessagePublisher messagePublisher;

    @Bean
    public List<Plugin> plugins() {
        List<PluginEntity> entities = pluginsRepository.findAll();
        List<Plugin> plugins = new ArrayList<>(entities.size());

        entities.forEach(entity -> {
            try {
                log.info("Building plugin: {} {} {}", entity.getPluginUuid(), entity.getName(), entity.getBuilderClass());
                Plugin plugin = buildPlugin(entity);
                plugins.add(plugin);
            } catch(ReflectiveOperationException e) {
                log.error("Failed to instantiate plugin builder!", e);
            }
        });

        log.info("Built plugins:\n"
                + plugins.stream().map(plugin -> plugin.getUuid() + " - " + plugin.getName()).collect(Collectors.joining("\n"))
        );

        return plugins;
    }

    private Plugin buildPlugin(PluginEntity pluginEntity) throws ReflectiveOperationException {
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
        Class<?> builderClass = ThingsConfiguration.class.getClassLoader().loadClass(builderClassName);
        return (PluginBuilder) builderClass.newInstance();
    }
}
