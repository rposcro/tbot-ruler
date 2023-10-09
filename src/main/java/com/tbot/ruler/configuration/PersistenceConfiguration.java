package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.json.JsonFileBindingsRepository;
import com.tbot.ruler.persistance.json.JsonFilePluginsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@Slf4j
@Configuration
public class PersistenceConfiguration {

    @Value("${ruler.thingsConfig.path:@null}")
    private String filaConfigPath;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private JsonFilePluginsRepository jsonFilePluginsRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private JsonFileBindingsRepository jsonFileBindingsRepository;

    @EventListener
    public void loadConfigData(ContextRefreshedEvent e) {
        if (filaConfigPath != null) {
            log.info("File based config found, loading into repository ...");
            loadPlugins();
            loadBindings();
        } else {
            log.info("No file based config to logs, skipped");
        }
    }

    private void loadPlugins() {
        log.info("Loading plugins data ...");
        List<PluginEntity> pluginEntites = jsonFilePluginsRepository.findAll();
        pluginEntites.stream()
                .forEach(entity -> {
                    if (!pluginsRepository.findByUuid(entity.getPluginUuid()).isPresent()) {
                        pluginsRepository.save(entity);
                    }
                });
    }

    private void loadBindings() {
        log.info("Loading bindings data ...");
        List<BindingEntity> bindingEntities = jsonFileBindingsRepository.findAll();
        bindingEntities.stream()
                .forEach(entity -> {
                    bindingsRepository.insert(entity);
                });
    }
}
