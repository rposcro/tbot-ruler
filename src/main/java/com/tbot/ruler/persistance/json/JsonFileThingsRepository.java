package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonFileThingsRepository extends AbstractJsonFileRepository implements ThingsRepository {

    private final JsonFileRepositoryReader repositoryReader;
    private final Map<String, List<ThingEntity>> entitiesMap;

    @Builder
    public JsonFileThingsRepository(JsonFileRepositoryReader repositoryReader) {
        this.repositoryReader = repositoryReader;
        this.entitiesMap = new HashMap<>();
    }

    @Override
    public List<ThingEntity> findByPluginUuid(String pluginUuid) {
        List<ThingEntity> entities = entitiesMap.get(pluginUuid);

        if (entities == null) {
            entities = readThingsForPlugin(pluginUuid);
            entitiesMap.put(pluginUuid, entities);
        }

        return entities;
    }

    private List<ThingEntity> readThingsForPlugin(String pluginUuid) {
        ThingPluginDTO pluginDTO = repositoryReader.getPluginDTO(pluginUuid);
        return repositoryReader.getThingDTOs().stream()
                .filter(thingDTO -> pluginDTO.getAlias().equals(thingDTO.getPluginAlias()))
                .map(thingDTO -> toEntity(thingDTO, pluginUuid))
                .collect(Collectors.toList());
    }

    private ThingEntity toEntity(ThingDTO dto, String pluginUuid) {
        return ThingEntity.builder()
                .thingId(nextId())
                .thingUuid(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .pluginUuid(pluginUuid)
                .configuration(dto.getConfigurationNode())
                .build();
    }
}
