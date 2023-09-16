package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.AppliancesRepository;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonFileAppliancesRepository extends AbstractJsonFileRepository implements AppliancesRepository {

    private final JsonFileRepositoryReader repositoryReader;

    private final List<ApplianceEntity> appliances;
    private final Map<String, ApplianceEntity> appliancesByUuid;

    @Builder
    public JsonFileAppliancesRepository(JsonFileRepositoryReader repositoryReader) {
        this.repositoryReader = repositoryReader;
        this.appliances = new LinkedList<>();
        this.appliancesByUuid = new HashMap<>();
    }

    @Override
    public List<ApplianceEntity> findAll() {
        ensureAll();
        return appliances;
    }

    @Override
    public Optional<ApplianceEntity> findByUuid(String thingUuid) {
        ensureAll();
        return Optional.ofNullable(appliancesByUuid.get(thingUuid));
    }

    private void ensureAll() {
        if (appliances.isEmpty()) {
            repositoryReader.getApplianceDTOs().stream()
                    .map(this::toEntity)
                    .forEach(entity -> {
                        appliances.add(entity);
                        appliancesByUuid.put(entity.getApplianceUuid(), entity);
                    });
        }
    }

    private ApplianceEntity toEntity(ApplianceDTO dto) {
        return ApplianceEntity.builder()
                .applianceId(nextId())
                .applianceUuid(dto.getUuid())
                .applianceType(dto.getType())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
