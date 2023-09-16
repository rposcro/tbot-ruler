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
import java.util.Optional;

public class JsonFileActuatorsRepository extends AbstractJsonFileRepository implements ActuatorsRepository {

    private final JsonFileRepositoryReader repositoryReader;

    private final List<ActuatorEntity> actuators;
    private final Map<String, ActuatorEntity> actuatorsByUuid;
    private final Map<String, List<ActuatorEntity>> actuatorsByThingUuid;

    @Builder
    public JsonFileActuatorsRepository(JsonFileRepositoryReader repositoryReader) {
        this.repositoryReader = repositoryReader;
        this.actuators = new LinkedList<>();
        this.actuatorsByUuid = new HashMap<>();
        this.actuatorsByThingUuid = new HashMap<>();
    }

    @Override
    public Optional<ActuatorEntity> findByUuid(String actuatorUuid) {
        ensureAll();
        return Optional.ofNullable(actuatorsByUuid.get(actuatorUuid));
    }

    @Override
    public List<ActuatorEntity> findAll() {
        return actuators;
    }

    @Override
    public List<ActuatorEntity> findByThingUuid(String thingUuid) {
        List<ActuatorEntity> entities = actuatorsByThingUuid.get(thingUuid);

        if (entities == null) {
            entities = readActuatorsForThing(thingUuid);
            actuatorsByThingUuid.put(thingUuid, entities);
        }

        return entities;
    }

    private void ensureAll() {
        if (actuators.isEmpty()) {
            repositoryReader.getThingDTOs().stream()
                    .map(ThingDTO::getUuid)
                    .flatMap(pluginUuid -> findByThingUuid(pluginUuid).stream())
                    .forEach(actuatorEntity -> {
                        actuators.add(actuatorEntity);
                        actuatorsByUuid.put(actuatorEntity.getThingUuid(), actuatorEntity);
                    });
        }
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
                .actuatorUuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .reference(dto.getRef())
                .thingUuid(thingUuid)
                .configuration(dto.getConfigurationNode())
                .build();
    }
}
