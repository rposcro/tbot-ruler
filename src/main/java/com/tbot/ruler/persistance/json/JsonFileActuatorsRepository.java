package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonFileActuatorsRepository extends AbstractJsonFileRepository {

    private final JsonFileRepositoryReader repositoryReader;

    private List<ActuatorEntity> actuators;
    private Map<String, List<ActuatorEntity>> actuatorsByThingUuid;

    @Builder
    public JsonFileActuatorsRepository(JsonFileRepositoryReader repositoryReader) {
        this.repositoryReader = repositoryReader;
    }

    public List<ActuatorEntity> findByThingUuid(String thingUuid) {
        ensureAll();
        return actuatorsByThingUuid.get(thingUuid);
    }

    private void ensureAll() {
        if (actuators == null) {
            actuators = new LinkedList<>();
            actuatorsByThingUuid = new HashMap();
            repositoryReader.getThingDTOs().stream()
                    .forEach(thingDTO -> {
                        thingDTO.getActuators().forEach(actuatorDTO -> {
                            ActuatorEntity actuatorEntity = toEntity(actuatorDTO);
                            actuators.add(actuatorEntity);
                            List<ActuatorEntity> actuatorEntities = actuatorsByThingUuid.get(thingDTO.getUuid());
                            if (actuatorEntities == null) {
                                actuatorEntities = new LinkedList<>();
                                actuatorsByThingUuid.put(thingDTO.getUuid(), actuatorEntities);
                            }
                            actuatorEntities.add(actuatorEntity);
                        });
                    });
        }
    }

    private ActuatorEntity toEntity(ActuatorDTO dto) {
        return ActuatorEntity.builder()
                .actuatorId(nextId())
                .actuatorUuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .reference(dto.getRef())
                .configuration(dto.getConfigurationNode())
                .build();
    }
}
