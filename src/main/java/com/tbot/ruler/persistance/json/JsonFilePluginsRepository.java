package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.json.dto.ThingPluginDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonFilePluginsRepository extends AbstractJsonFileRepository {

    private final JsonFileThingsRepository thingsRepository;
    private final List<PluginEntity> plugins;
    private final Map<String, PluginEntity> pluginsByReference;

    @Builder
    public JsonFilePluginsRepository(JsonFileRepositoryReader repositoryReader, JsonFileThingsRepository thingsRepository) {
        this.thingsRepository = thingsRepository;
        this.plugins = Collections.unmodifiableList(
                repositoryReader.getPluginDTOs().stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList())
        );
        this.pluginsByReference = plugins.stream()
                .collect(Collectors.toMap(PluginEntity::getName, Function.identity()));
    }

    public List<PluginEntity> findAll() {
        return plugins;
    }

    public PluginEntity findByReference(String reference) {
        return pluginsByReference.get(reference);
    }

    private PluginEntity toEntity(ThingPluginDTO pluginDTO) {
        return PluginEntity.builder()
                .pluginId(nextId())
                .pluginUuid(pluginDTO.getUuid())
                .builderClass(pluginDTO.getBuilderClass())
                .name(pluginDTO.getAlias())
                .configuration(pluginDTO.getConfigurationNode())
                .things(thingsRepository.findByPluginUuid(pluginDTO.getUuid()))
                .build();
    }
}
