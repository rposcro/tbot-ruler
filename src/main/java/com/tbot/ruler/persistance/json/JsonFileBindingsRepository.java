package com.tbot.ruler.persistance.json;

import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonFileBindingsRepository extends AbstractJsonFileRepository implements BindingsRepository {

    private final JsonFileRepositoryReader repositoryReader;
    private final Map<String, List<BindingEntity>> entitiesMap;

    @Builder
    public JsonFileBindingsRepository(JsonFileRepositoryReader repositoryReader) {
        this.repositoryReader = repositoryReader;
        this.entitiesMap = new HashMap<>();
    }

    @Override
    public List<BindingEntity> findBySenderUuid(String senderUuid) {
        List<BindingEntity> entities = entitiesMap.get(senderUuid);

        if (entities == null) {
            entities = readBindingsForSender(senderUuid);
            entitiesMap.put(senderUuid, entities);
        }

        return entities;
    }

    private List<BindingEntity> readBindingsForSender(String senderUuid) {
        return repositoryReader.getBindingDTOs().stream()
                .filter(dto -> senderUuid.equals(dto.getSenderId()))
                .map(dto -> toEntities(dto))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<BindingEntity> toEntities(BindingDTO dto) {
        return dto.getConsumerIds().stream()
                .map(consumerId -> BindingEntity.builder()
                        .senderUuid(dto.getSenderId())
                        .receiverUuid(consumerId)
                        .build())
                .collect(Collectors.toList());
    }
}
