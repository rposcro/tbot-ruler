package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.json.JsonFilePluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    @Value("${ruler.thingsConfig.path:@null}")
    private String filaConfigPath;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private JsonFilePluginsRepository jsonFilePluginsRepository;

    @EventListener
    public void loadConfigData(ApplicationContextInitializedEvent applicationEvent) {
        if (filaConfigPath != null) {
            log.info("File based config found, loading into repository ...");
            loadPlugins();
        } else {
            log.info("No file based config to logs, skipped");
        }
    }

    private void loadPlugins() {
        log.info("Loading plugins data ...");
        jsonFilePluginsRepository.findAll().stream()
                .forEach(entity -> {
                    if (pluginsRepository.fundByUuid(entity.getPluginUuid()).isPresent()) {
                        pluginsRepository.save(entity);
                    }
                });
    }
}
