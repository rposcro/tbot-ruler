package com.tbot.ruler.service.dump;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.StencilsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.persistance.json.dto.BindingDTO;
import com.tbot.ruler.persistance.json.dto.PluginDTO;
import com.tbot.ruler.persistance.json.dto.StencilDTO;
import com.tbot.ruler.persistance.json.dto.ThingDTO;
import com.tbot.ruler.persistance.json.dto.WebhookDTO;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.StencilEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

@Slf4j
@Service
public class DumpJsonService {

    private final static DateTimeFormatter DT_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral('-')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral('-')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral('-')
            .appendValue(NANO_OF_SECOND, 9)
            .toFormatter();
    private final static PrettyPrinter JSON_PRINTER = new DefaultPrettyPrinter()
            .withObjectIndenter(new DefaultIndenter("    ", DefaultIndenter.SYS_LF))
            .withArrayIndenter(new DefaultIndenter("    ", DefaultIndenter.SYS_LF));

    @Value("${ruler.fileRepository.dump.jsonFiles:@null}")
    private String dumpJsonFiles;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private WebhooksRepository webhooksRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private StencilsRepository stencilsRepository;

    public void dumpToJson() {
        if (dumpJsonFiles == null) {
            throw new ConfigurationException("Json dump path not set, try setting 'ruler.dump.jsonFiles' property");
        }

        try {
            DumpContext dumpContext = buildContext();
            ensureDirectory(dumpContext);
            dumpPlugins(dumpContext);
            dumpThings(dumpContext);
            dumpActuators(dumpContext);
            dumpWebhooks(dumpContext);
            dumpBindings(dumpContext);
            dumpStencils(dumpContext);
        } catch(IOException e) {
            throw new ConfigurationException("Could not use the file system to store json dumps!", e);
        }
    }

    private void dumpPlugins(DumpContext dumpContext) throws IOException {
        List<PluginDTO> plugins = dumpContext.pluginEntitiesMap.values().stream()
                .map(entity -> PluginDTO.builder()
                        .uuid(entity.getPluginUuid())
                        .alias(entity.getName())
                        .factoryClass(entity.getFactoryClass())
                        .configuration(entity.getConfiguration())
                        .build())
                .collect(Collectors.toList());
        File file = formatFile("plugins", dumpContext);
        objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
            public List<PluginDTO> getPlugins() {
                return plugins;
            }
        });
        log.info("Dumped plugins to {}", file);
    }

    private void dumpThings(DumpContext dumpContext) throws IOException {
        List<ThingDTO> things = thingsRepository.findAll().stream()
                .map(thingEntity -> ThingDTO.builder()
                        .uuid(thingEntity.getThingUuid())
                        .name(thingEntity.getName())
                        .description(thingEntity.getDescription())
                        .configuration(thingEntity.getConfiguration())
                        .build())
                .collect(Collectors.toList());
        File file = formatFile("things", dumpContext);
        objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
                public List<ThingDTO> getThings() {
                    return things;
                }
            });
        log.info("Dumped things to {}", file);
    }

    private void dumpActuators(DumpContext dumpContext) throws IOException {
        for (ThingEntity thingEntity : thingsRepository.findAll()) {
            List<ActuatorDTO> actuators = actuatorsRepository.findByThingId(thingEntity.getThingId()).stream()
                    .map(actuatorEntity -> ActuatorDTO.builder()
                            .uuid(actuatorEntity.getActuatorUuid())
                            .thingUuid(thingEntity.getThingUuid())
                            .pluginUuid(dumpContext.pluginEntitiesMap.get(actuatorEntity.getPluginId()).getPluginUuid())
                            .reference(actuatorEntity.getReference())
                            .name(actuatorEntity.getName())
                            .description(actuatorEntity.getDescription())
                            .configuration(actuatorEntity.getConfiguration())
                            .build())
                    .collect(Collectors.toList());
            File file = formatFile("actuators-" + thingEntity.getName().replace(' ', '_'), dumpContext);
            objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
                public List<ActuatorDTO> getActuators() {
                    return actuators;
                }
            });
            log.info("Dumped actuators to {}", file);
        }
    }

    private void dumpWebhooks(DumpContext dumpContext) throws IOException {
        List<WebhookDTO> webhooks = webhooksRepository.findAll().stream()
                .map(webhookEntity -> WebhookDTO.builder()
                        .uuid(webhookEntity.getWebhookUuid())
                        .owner(webhookEntity.getOwner())
                        .name(webhookEntity.getName())
                        .description(webhookEntity.getDescription())
                        .build())
                .collect(Collectors.toList());
        Map<String, List<WebhookDTO>> webhooksMap = webhooks.stream()
                .collect(Collectors.groupingBy(WebhookDTO::getOwner));
        for (String owner: webhooksMap.keySet()) {
            File file = formatFile("webhooks-" + owner.replace(' ', '_'), dumpContext);
            objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
                public List<WebhookDTO> getWebhooks() {
                    return webhooksMap.get(owner);
                }
            });
            log.info("Dumped webhooks to {}", file);
        }
    }

    private void dumpBindings(DumpContext dumpContext) throws IOException {
        Map<String, BindingDTO> bindingsMap = new HashMap<>();
        bindingsRepository.findAll().forEach(bindingEntity -> {
            BindingDTO dto = bindingsMap.get(bindingEntity.getSenderUuid());
            if (dto == null) {
                dto = BindingDTO.builder()
                        .senderUuid(bindingEntity.getSenderUuid())
                        .receiversUuid(new LinkedList<>())
                        .build();
                bindingsMap.put(bindingEntity.getSenderUuid(), dto);
            }
            dto.getReceiversUuid().add(bindingEntity.getReceiverUuid());
        });
        File file = formatFile("bindings", dumpContext);
        objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
            public Collection<BindingDTO> getBindings() {
                return bindingsMap.values();
            }
        });
        log.info("Dumped bindings to {}", file);
    }

    private void dumpStencils(DumpContext dumpContext) throws IOException {
        List<StencilEntity> stencilsEntities = stencilsRepository.findAll();
        for (StencilEntity stencilEntity : stencilsEntities) {
            StencilDTO dto = StencilDTO.builder()
                    .uuid(stencilEntity.getStencilUuid())
                    .owner(stencilEntity.getOwner())
                    .type(stencilEntity.getType())
                    .payload(stencilEntity.getPayload())
                    .build();
            File file = formatFile(format("stencil-%s-%s", stencilEntity.getOwner(), stencilEntity.getType()), dumpContext);
            objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
                public List<StencilDTO> getStencils() {
                    return Collections.singletonList(dto);
                }
            });
            log.info("Dumped stencil to {}", file);
        }
    }

    private DumpContext buildContext() {
        LocalDateTime now = LocalDateTime.now();
        return DumpContext.builder()
                .dumpDate(now)
                .dumpDirectory(Path.of(dumpJsonFiles, now.format(DT_FORMATTER)))
                .pluginEntitiesMap(pluginsRepository.findAll().stream()
                        .collect(Collectors.toMap(PluginEntity::getPluginId, Function.identity())))
                .bindingEntities(bindingsRepository.findAll())
                .build();
    }

    private void ensureDirectory(DumpContext dumpContext) throws IOException {
        if (Files.exists(dumpContext.dumpDirectory)) {
            throw new IOException("Json dump directory already exists: " + dumpContext.dumpDirectory);
        }
        Files.createDirectories(dumpContext.dumpDirectory);
    }

    private File formatFile(String baseName, DumpContext dumpContext) {
        String fileName = format("%s-%s.json", dumpContext.dumpDate.toEpochSecond(ZoneOffset.UTC), baseName);
        return dumpContext.dumpDirectory.resolve(fileName).toFile();
    }

    @Builder
    private static class DumpContext {
        private LocalDateTime dumpDate;
        private Path dumpDirectory;
        private Map<Long, PluginEntity> pluginEntitiesMap;
        private List<BindingEntity> bindingEntities;
    }
}
