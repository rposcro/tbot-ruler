package com.tbot.ruler.service.dump;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.SchemasRepository;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.persistance.json.dto.BindingDTO;
import com.tbot.ruler.persistance.json.dto.PluginDTO;
import com.tbot.ruler.persistance.json.dto.SchemaDTO;
import com.tbot.ruler.persistance.json.dto.ThingDTO;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.SchemaEntity;
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

    @Value("${ruler.dump.jsonFiles:@null}")
    private String dumpJsonFiles;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private SchemasRepository schemasRepository;

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
            dumpBindings(dumpContext);
            dumpSchemas(dumpContext);
        } catch(IOException e) {
            throw new ConfigurationException("Could not use the file system to store json dumps!", e);
        }
    }

    private void dumpPlugins(DumpContext dumpContext) throws IOException {
        List<PluginDTO> plugins = dumpContext.pluginEntities.stream()
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
        for (PluginEntity pluginEntity: dumpContext.pluginEntities) {
            List<ThingDTO> things = pluginEntity.getThings().stream()
                    .map(thingEntity -> ThingDTO.builder()
                            .uuid(thingEntity.getThingUuid())
                            .pluginUuid(pluginEntity.getPluginUuid())
                            .name(thingEntity.getName())
                            .description(thingEntity.getDescription())
                            .configuration(thingEntity.getConfiguration())
                            .build())
                    .collect(Collectors.toList());
            File file = formatFile("things-" + pluginEntity.getName().replace(' ', '_'), dumpContext);
            objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
                public List<ThingDTO> getThings() {
                    return things;
                }
            });
            log.info("Dumped things to {}", file);
        }
    }

    private void dumpActuators(DumpContext dumpContext) throws IOException {
        List<ThingEntity> thingEntities = dumpContext.pluginEntities.stream()
                .flatMap(pluginEntity -> pluginEntity.getThings().stream())
                .collect(Collectors.toList());
        for (ThingEntity thingEntity: thingEntities) {
            List<ActuatorDTO> actuators = thingEntity.getActuators().stream()
                    .map(actuatorEntity -> ActuatorDTO.builder()
                            .uuid(actuatorEntity.getActuatorUuid())
                            .thingUuid(thingEntity.getThingUuid())
                            .name(actuatorEntity.getName())
                            .ref(actuatorEntity.getReference())
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

    private void dumpSchemas(DumpContext dumpContext) throws IOException {
        List<SchemaEntity> schemaEntities = schemasRepository.findAll();
        for (SchemaEntity schemaEntity: schemaEntities) {
            SchemaDTO dto = SchemaDTO.builder()
                    .uuid(schemaEntity.getSchemaUuid())
                    .owner(schemaEntity.getOwner())
                    .type(schemaEntity.getType())
                    .payload(schemaEntity.getPayload())
                    .build();
            File file = formatFile(format("schema-%s-%s", schemaEntity.getOwner(), schemaEntity.getType()), dumpContext);
            objectMapper.writer(JSON_PRINTER).writeValue(file, new Object() {
                public List<SchemaDTO> getSchemas() {
                    return Collections.singletonList(dto);
                }
            });
            log.info("Dumped schema to {}", file);
        }
    }

    private DumpContext buildContext() {
        LocalDateTime now = LocalDateTime.now();
        return DumpContext.builder()
                .dumpDate(now)
                .dumpDirectory(Path.of(dumpJsonFiles, now.format(DT_FORMATTER)))
                .pluginEntities(pluginsRepository.findAll())
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
        private List<PluginEntity> pluginEntities;
        private List<BindingEntity> bindingEntities;
    }
}
