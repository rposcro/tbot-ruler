package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JsonFilePluginsRepository extends AbstractJsonFileRepository implements PluginsRepository {

    private final JsonFileThingsRepository thingsRepository;
    private final List<PluginEntity> entityList;

    @Builder
    public JsonFilePluginsRepository(JsonFileRepositoryReader repositoryReader, JsonFileThingsRepository thingsRepository) {
        this.thingsRepository = thingsRepository;
        this.entityList = Collections.unmodifiableList(
                repositoryReader.getPluginDTOs().stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<PluginEntity> findAll() {
        return entityList;
    }

    private PluginEntity toEntity(ThingPluginDTO pluginDTO) {
        return PluginEntity.builder()
                .pluginId(nextId())
                .pluginUuid(pluginDTO.getUuid())
                .builderClass(pluginDTO.getBuilderClass())
                .name(pluginDTO.getAlias())
                .things(thingsRepository.findByPluginUuid(pluginDTO.getUuid()))
                .build();
    }
}
