package com.tbot.ruler.service.dump;

import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.StencilsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.json.JsonFileRepositoryReader;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.StencilEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.persistance.model.WebhookEntity;
import com.tbot.ruler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonRepositoryService implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${ruler.fileRepository.load.path:@null}")
    private String repositoryPath;

    @Value("${ruler.fileRepository.load.loadOnStartup:false}")
    private boolean loadOnStartup;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private WebhooksRepository webhooksRepository;

    @Autowired
    private StencilsRepository stencilsRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent e) {
        if (loadOnStartup) {
            loadJsonRepository();
        }
    }

    public void loadJsonRepository() {
        if (repositoryPath == null) {
            throw new ConfigurationException("JSON repository path is not specified!");
        }

        JsonFileRepositoryReader jsonFileRepositoryReader = new JsonFileRepositoryReader(repositoryPath, fileUtil);

        log.info("File based config found, loading into repository ...");
        loadPlugins(jsonFileRepositoryReader);
        loadThings(jsonFileRepositoryReader);
        loadActuators(jsonFileRepositoryReader);
        loadWebhooks(jsonFileRepositoryReader);
        loadBindings(jsonFileRepositoryReader);
        loadStencils(jsonFileRepositoryReader);
    }

    private void loadPlugins(JsonFileRepositoryReader jsonFileRepositoryReader) {
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
                            .factoryClass(dto.getFactoryClass())
                            .configuration(dto.getConfiguration())
                            .build();
                    pluginsRepository.save(pluginEntity);
                    log.info("Loaded plugin {}", dto.getUuid());
                });
    }

    private void loadThings(JsonFileRepositoryReader jsonFileRepositoryReader) {
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
                    ThingEntity thingEntity = ThingEntity.builder()
                            .thingUuid(dto.getUuid())
                            .name(dto.getName())
                            .description(dto.getDescription())
                            .configuration(dto.getConfiguration())
                            .build();
                    thingsRepository.save(thingEntity);
                    log.info("Loaded thing {}", dto.getUuid());
                });
    }

    private void loadActuators(JsonFileRepositoryReader jsonFileRepositoryReader) {
        log.info("Loading actuators ...");
        jsonFileRepositoryReader.getActuatorDTOs().stream()
                .filter(this::checkActuator)
                .forEach(dto -> {
                    PluginEntity pluginEntity = pluginsRepository.findByUuid(dto.getPluginUuid()).orElse(null);
                    ThingEntity thingEntity = thingsRepository.findByUuid(dto.getThingUuid()).orElse(null);
                    if (thingEntity != null) {
                        ActuatorEntity actuatorEntity = ActuatorEntity.builder()
                                .actuatorUuid(dto.getUuid())
                                .pluginId(pluginEntity.getPluginId())
                                .thingId(thingEntity.getThingId())
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .configuration(dto.getConfiguration())
                                .reference(dto.getReference())
                                .build();
                        actuatorsRepository.save(actuatorEntity);
                        log.info("Loaded actuator {}", dto.getUuid());
                    } else {
                        log.info("Skipped actuator {} due to unknown thing {}", dto.getUuid(), dto.getThingUuid());
                    }
                });
    }

    private void loadWebhooks(JsonFileRepositoryReader jsonFileRepositoryReader) {
        log.info("Loading webhooks ...");
        jsonFileRepositoryReader.getWebhookDTOs().stream()
                .filter(dto -> {
                    if (!webhooksRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped webhook {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    WebhookEntity webhookEntity = WebhookEntity.builder()
                            .webhookUuid(dto.getUuid())
                            .owner(dto.getOwner())
                            .name(dto.getName())
                            .description(dto.getDescription())
                            .build();
                    webhooksRepository.save(webhookEntity);
                    log.info("Loaded webhook {}", dto.getUuid());
                });
    }

    private boolean checkActuator(ActuatorDTO actuatorDTO) {
        if (actuatorsRepository.findByUuid(actuatorDTO.getUuid()).isPresent()) {
            log.info("Skipped actuator {}, already exists", actuatorDTO.getUuid());
            return false;
        } else if (!pluginsRepository.findByUuid(actuatorDTO.getPluginUuid()).isPresent()) {
            log.info("Skipped actuator {}, no related plugin {} found", actuatorDTO.getUuid(), actuatorDTO.getPluginUuid());
            return false;
        } else if (!thingsRepository.findByUuid(actuatorDTO.getThingUuid()).isPresent()) {
            log.info("Skipped actuator {}, no related thing {} found", actuatorDTO.getUuid(), actuatorDTO.getThingUuid());
            return false;
        }
        return true;
    }

    private void loadBindings(JsonFileRepositoryReader jsonFileRepositoryReader) {
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

    private void loadStencils(JsonFileRepositoryReader jsonFileRepositoryReader) {
        log.info("Loading stencils data ...");
        jsonFileRepositoryReader.getStencilDTOs().stream()
                .filter(dto -> {
                    if (!stencilsRepository.findByUuid(dto.getUuid()).isPresent()) {
                        return true;
                    } else {
                        log.info("Skipped stencil {}, already exists", dto.getUuid());
                        return false;
                    }
                })
                .forEach(dto -> {
                    StencilEntity stencilEntity = StencilEntity.builder()
                            .stencilUuid(dto.getUuid())
                            .owner(dto.getOwner())
                            .type(dto.getType())
                            .payload(dto.getPayload())
                            .build();
                    stencilsRepository.save(stencilEntity);
                    log.info("Loaded stencil {}", dto.getUuid());
                });
    }
}
