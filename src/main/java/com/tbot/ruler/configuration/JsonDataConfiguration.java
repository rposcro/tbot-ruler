package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.SchemasRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.json.JsonFileRepositoryReader;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.SchemaEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
public class JsonDataConfiguration {

    @Value("${ruler.thingsConfig.path:@null}")
    private String filaConfigPath;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private SchemasRepository schemasRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    private JsonFileRepositoryReader jsonFileRepositoryReader;

    @EventListener
    public void loadConfigData(ContextRefreshedEvent e) {
        this.jsonFileRepositoryReader = new JsonFileRepositoryReader(filaConfigPath, fileUtil);

        if (filaConfigPath != null) {
            log.info("File based config found, loading into repository ...");
            loadPlugins();
            loadThings();
            loadActuators();
            loadBindings();
            loadSchemas();
        } else {
            log.info("No file based config to load");
        }
    }

    private void loadPlugins() {
        log.info("Loading plugins data ...");
        jsonFileRepositoryReader.getPluginDTOs().stream()
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
                            .builderClass(dto.getBuilderClass())
                            .configuration(dto.getConfiguration())
                            .build();
                    pluginsRepository.save(pluginEntity);
                    log.info("Loaded plugin {}", dto.getUuid());
                });
    }

    private void loadThings() {
        log.info("Loading things data ...");
        jsonFileRepositoryReader.getThingDTOs().stream()
                .filter(dto -> {
                    if (!thingsRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped thing {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    PluginEntity pluginEntity = pluginsRepository.findByUuid(dto.getPluginUuid()).orElse(null);
                    if (pluginEntity != null) {
                        ThingEntity thingEntity = ThingEntity.builder()
                                .thingUuid(dto.getUuid())
                                .pluginId(pluginEntity.getPluginId())
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .configuration(dto.getConfiguration())
                                .build();
                        thingsRepository.save(thingEntity);
                        log.info("Loaded thing {}", dto.getUuid());
                    } else {
                        log.info("Skipped thing {} due to unknown plugin {}", dto.getUuid(), dto.getPluginUuid());
                    }
                });
    }

    private void loadActuators() {
        log.info("Loading actuators ...");
        jsonFileRepositoryReader.getActuatorDTOs().stream()
                .filter(dto -> {
                    if (!actuatorsRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped actuator {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    ThingEntity thingEntity = thingsRepository.findByUuid(dto.getThingUuid()).orElse(null);
                    if (thingEntity != null) {
                        ActuatorEntity actuatorEntity = ActuatorEntity.builder()
                                .actuatorUuid(dto.getUuid())
                                .thingId(thingEntity.getThingId())
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .configuration(dto.getConfiguration())
                                .reference(dto.getRef())
                                .build();
                        actuatorsRepository.save(actuatorEntity);
                        log.info("Loaded actuator {}", dto.getUuid());
                    } else {
                        log.info("Skipped actuator {} due to unknown thing {}", dto.getUuid(), dto.getThingUuid());
                    }
                });
    }

    private void loadBindings() {
        log.info("Loading bindings data ...");
        jsonFileRepositoryReader.getBindingDTOs().stream().forEach(dto -> {
            dto.getReceiversUuid().forEach(receiverUuid -> {
                BindingEntity bindingEntity = BindingEntity.builder()
                        .senderUuid(dto.getSenderUuid())
                        .receiverUuid(receiverUuid)
                        .build();
                if (!bindingsRepository.bindingExists(bindingEntity.getSenderUuid(), bindingEntity.getReceiverUuid())) {
                    bindingsRepository.insert(bindingEntity);
                    log.info("Loaded binding {} to {}", bindingEntity.getSenderUuid(), bindingEntity.getReceiverUuid());
                } else {
                    log.info("Skipped binding {} to {}, already exists", bindingEntity.getSenderUuid(), bindingEntity.getReceiverUuid());
                }
            });
        });
    }

    private void loadSchemas() {
        log.info("Loading schemas data ...");
        jsonFileRepositoryReader.getSchemaDTOs().stream()
                .filter(dto -> {
                    if (!schemasRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped schema {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    SchemaEntity schemaEntity = SchemaEntity.builder()
                            .schemaUuid(dto.getUuid())
                            .owner(dto.getOwner())
                            .type(dto.getType())
                            .payload(dto.getPayload())
                            .build();
                    schemasRepository.save(schemaEntity);
                    log.info("Loaded schema {}", dto.getUuid());
                });
    }
}
