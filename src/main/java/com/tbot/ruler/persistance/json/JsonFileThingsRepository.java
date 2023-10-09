package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.json.dto.ThingPluginDTO;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.persistance.json.dto.ThingDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonFileThingsRepository extends AbstractJsonFileRepository {

    private final JsonFileActuatorsRepository actuatorsRepository;
    private final JsonFileRepositoryReader repositoryReader;

    private List<ThingEntity> things;
    private Map<String, List<ThingEntity>> thingsByPluginUuid;

    @Builder
    public JsonFileThingsRepository(
            JsonFileActuatorsRepository actuatorsRepository,
            JsonFileRepositoryReader repositoryReader) {
        this.actuatorsRepository = actuatorsRepository;
        this.repositoryReader = repositoryReader;
    }

    public List<ThingEntity> findByPluginUuid(String pluginUuid) {
        ensureAll();
        return thingsByPluginUuid.get(pluginUuid);
    }

    private void ensureAll() {
        if (things == null) {
            things = new LinkedList<>();
            thingsByPluginUuid = new HashMap<>();
            repositoryReader.getThingDTOs()
                    .forEach(thingDTO -> {
                        ThingEntity thingEntity = toEntity(thingDTO);
                        ThingPluginDTO pluginDTO = repositoryReader.getPluginDTOByReference(thingDTO.getPluginAlias());
                        things.add(thingEntity);
                        List<ThingEntity> thingEntities = thingsByPluginUuid.get(pluginDTO.getUuid());
                        if (thingEntities == null) {
                            thingEntities = new LinkedList<>();
                            thingsByPluginUuid.put(pluginDTO.getUuid(), thingEntities);
                        }
                        thingEntities.add(thingEntity);
                    });
        }
    }

    private ThingEntity toEntity(ThingDTO dto) {
        return ThingEntity.builder()
                .thingId(nextId())
                .thingUuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .configuration(dto.getConfigurationNode())
                .actuators(actuatorsRepository.findByThingUuid(dto.getUuid()))
                .build();
    }
}
