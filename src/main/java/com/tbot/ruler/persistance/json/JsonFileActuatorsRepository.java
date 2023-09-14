package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonFileActuatorsRepository extends AbstractJsonFileRepository implements ActuatorsRepository {

    private final JsonFileRepositoryReader repositoryReader;
    private final Map<String, List<ActuatorEntity>> entitiesMap;

    @Builder
    public JsonFileActuatorsRepository(JsonFileRepositoryReader repositoryReader) {
        this.repositoryReader = repositoryReader;
        this.entitiesMap = new HashMap<>();
    }

    @Override
    public List<ActuatorEntity> findByThingUuid(String thingUuid) {
        List<ActuatorEntity> entities = entitiesMap.get(thingUuid);

        if (entities == null) {
            entities = readActuatorsForThing(thingUuid);
            entitiesMap.put(thingUuid, entities);
        }

        return entities;
    }

    private List<ActuatorEntity> readActuatorsForThing(String thingUuid) {
        ThingDTO thingDTO = repositoryReader.getThingDTO(thingUuid);
        List<ActuatorEntity> entities = new LinkedList<>();

        thingDTO.getActuators().stream()
                .map(dto -> toEntity(dto, thingUuid))
                .forEach(entity -> entities.add(entity));

        return entities;
    }

    private ActuatorEntity toEntity(ActuatorDTO dto, String thingUuid) {
        return ActuatorEntity.builder()
                .actuatorId(nextId())
                .actuatorUuid(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .reference(dto.getRef())
                .thingUuid(thingUuid)
                .configuration(dto.getConfigurationNode())
                .build();
    }
}
