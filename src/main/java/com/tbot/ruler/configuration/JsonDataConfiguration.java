package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.json.JsonFileBindingsRepository;
import com.tbot.ruler.persistance.json.JsonFilePluginsRepository;
import com.tbot.ruler.persistance.json.JsonFileRepositoryReader;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.persistance.json.dto.PluginDTO;
import com.tbot.ruler.persistance.json.dto.ThingDTO;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
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
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private JsonFileBindingsRepository jsonFileBindingsRepository;

    @Autowired
    private JsonFileRepositoryReader jsonFileRepositoryReader;

    @EventListener
    public void loadConfigData(ContextRefreshedEvent e) {
        if (filaConfigPath != null) {
            log.info("File based config found, loading into repository ...");
            loadPlugins();
            loadThings();
            loadBindings();
        } else {
            log.info("No file based config to load");
        }
    }

    private void loadPlugins() {
        log.info("Loading plugins data ...");
        List<PluginDTO> pluginDTOs = jsonFileRepositoryReader.getPluginDTOs();
        pluginDTOs.stream()
                .filter(dto -> {
                    if (!pluginsRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped plugin {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    PluginEntity pluginEntity = PluginEntity.builder()
                            .pluginUuid(dto.getUuid())
                            .name(dto.getAlias())
                            .configuration(dto.getConfigurationNode())
                            .build();
                    pluginsRepository.save(pluginEntity);
                    log.info("Loaded plugin {}", dto.getUuid());
                });
    }

    private void loadThings() {
        log.info("Loading things data ...");
        List<ThingDTO> thingDTOs = jsonFileRepositoryReader.getThingDTOs();
        thingDTOs.stream()
                .filter(dto -> {
                    if (!thingsRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped thing {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    PluginDTO pluginDTO = jsonFileRepositoryReader.getPluginDTOByReference(dto.getPluginAlias());
                    PluginEntity pluginEntity = pluginDTO == null ?
                            null : pluginsRepository.findByUuid(pluginDTO.getUuid()).orElse(null);
                    if (pluginEntity != null) {
                        ThingEntity thingEntity = ThingEntity.builder()
                                .thingUuid(dto.getUuid())
                                .pluginId(pluginEntity.getPluginId())
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .configuration(dto.getConfigurationNode())
                                .build();
                        thingEntity = thingsRepository.save(thingEntity);
                        log.info("Loaded thing {}", dto.getUuid());
                        loadActuators(dto.getActuators(), thingEntity);
                    } else {
                        log.info("Skipped thing {} due to unknown plugin {}", dto.getUuid(), dto.getPluginAlias());
                    }
                });
    }

    private void loadActuators(List<ActuatorDTO> actuatorDTOs, ThingEntity thingEntity) {
        log.info("Loading actuators data for thing {} ...", thingEntity.getThingUuid());
        actuatorDTOs.stream()
                .filter(dto -> {
                    if (!actuatorsRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped actuator {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    ActuatorEntity actuatorEntity = ActuatorEntity.builder()
                            .actuatorUuid(dto.getUuid())
                            .name(dto.getName())
                            .description(dto.getDescription())
                            .configuration(dto.getConfigurationNode())
                            .build();
                    actuatorsRepository.save(actuatorEntity);
                    log.info("Loaded actuator {}", dto.getUuid());
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
