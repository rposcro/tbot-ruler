package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonFileThingsRepository extends AbstractJsonFileRepository implements ThingsRepository {

    private final JsonFileActuatorsRepository actuatorsRepository;
    private final JsonFileRepositoryReader repositoryReader;

    private final List<ThingEntity> things;
    private final Map<String, ThingEntity> thingsByUuid;
    private final Map<String, List<ThingEntity>> thingsByPluginUuid;

    @Builder
    public JsonFileThingsRepository(JsonFileActuatorsRepository actuatorsRepository, JsonFileRepositoryReader repositoryReader) {
        this.actuatorsRepository = actuatorsRepository;
        this.repositoryReader = repositoryReader;
        this.things = new LinkedList<>();
        this.thingsByUuid = new HashMap<>();
        this.thingsByPluginUuid = new HashMap<>();
    }

    @Override
    public List<ThingEntity> findAll() {
        ensureAll();
        return things;
    }

    @Override
    public Optional<ThingEntity> findByUuid(String thingUuid) {
        ensureAll();
        return Optional.ofNullable(thingsByUuid.get(thingUuid));
    }

    @Override
    public List<ThingEntity> findByPluginUuid(String pluginUuid) {
        List<ThingEntity> entities = thingsByPluginUuid.get(pluginUuid);

        if (entities == null) {
            entities = readThingsForPlugin(pluginUuid);
            thingsByPluginUuid.put(pluginUuid, entities);
        }

        return entities;
    }

    private void ensureAll() {
        if (things.isEmpty()) {
            repositoryReader.getPluginDTOs().stream()
                    .map(ThingPluginDTO::getUuid)
                    .flatMap(pluginUuid -> findByPluginUuid(pluginUuid).stream())
                    .forEach(thingEntity -> {
                        things.add(thingEntity);
                        thingsByUuid.put(thingEntity.getThingUuid(), thingEntity);
                    });
        }
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
                .thingUuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .pluginUuid(pluginUuid)
                .configuration(dto.getConfigurationNode())
                .actuators(actuatorsRepository.findByThingUuid(dto.getUuid()))
                .build();
    }
}
