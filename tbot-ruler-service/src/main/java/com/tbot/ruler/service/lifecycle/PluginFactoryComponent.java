package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.plugins.PluginFactory;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PluginFactoryComponent {

    public Plugin buildPlugin(PluginEntity pluginEntity) {
        try {
            RulerPluginContext context = RulerPluginContext.builder()
                    .pluginUuid(pluginEntity.getPluginUuid())
                    .pluginName(pluginEntity.getName())
                    .pluginConfiguration(pluginEntity.getConfiguration())
                    .build();
            PluginFactory factory = instantiateFactory(pluginEntity);
            Plugin plugin = factory.producePlugin(context);
            log.info("Built plugin {}", plugin.getUuid());
            return plugin;
        } catch(ReflectiveOperationException e) {
            log.error("Failed to complete plugin builder of " + pluginEntity.getPluginUuid(), e);
            return null;
        }
    }

    private PluginFactory instantiateFactory(PluginEntity pluginEntity) throws ReflectiveOperationException {
        String builderClassName = pluginEntity.getFactoryClass();
        Class<?> builderClass = this.getClass().getClassLoader().loadClass(builderClassName);
        return (PluginFactory) builderClass.getConstructor().newInstance();
    }
}
