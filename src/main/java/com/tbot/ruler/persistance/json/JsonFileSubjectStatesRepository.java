package com.tbot.ruler.persistance.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.ServiceExecutionException;
import com.tbot.ruler.persistance.SubjectStatesRepository;
import com.tbot.ruler.persistance.model.SubjectStateEntity;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonFileSubjectStatesRepository implements SubjectStatesRepository {

    private final ObjectMapper objectMapper;
    private final Path statesFile;

    private Map<String, SubjectStateEntity> statesMap;

    @Builder
    public JsonFileSubjectStatesRepository(
            @Value("${ruler.cache.states.path}") String statesPath,
            ObjectMapper objectMapper) {
        this.statesFile = Paths.get(statesPath);
        this.objectMapper = objectMapper;
        initialize();
    }

    public void initialize() {
        if (!Files.exists(statesFile)) {
            statesMap = new HashMap<>();
            persistStatesMap();
        } else {
            statesMap = readStatesMap();
        }
    }

    @Override
    public Optional<SubjectStateEntity> findBySubjectUuid(String subjectUuid) {
        return Optional.ofNullable(statesMap.get(subjectUuid));
    }

    @Override
    public SubjectStateEntity save(SubjectStateEntity stateEntity) {
        statesMap.put(stateEntity.getSubjectUuid(), stateEntity);
        return stateEntity;
    }

    private Map<String, SubjectStateEntity> readStatesMap() {
        try {
            JsonNode rootNode = objectMapper.readTree(statesFile.toFile());
            Iterable<JsonNode> nodes = () -> rootNode.elements();
            return StreamSupport.stream(nodes.spliterator(), false)
                    .map(this::parseNode)
                    .collect(Collectors.toMap(SubjectStateEntity::getSubjectUuid, Function.identity()));
        } catch(IOException e) {
            throw new ServiceExecutionException("Could not read states from persistence file!", e);
        }
    }

    private SubjectStateEntity parseNode(JsonNode stateNode) {
        return SubjectStateEntity.builder()
                .subjectUuid(stateNode.get("key").asText())
                .payload(stateNode.get("value"))
                .build();
    }

    private void persistStatesMap() {
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValue(statesFile.toFile(), statesMap);
//        } catch(IOException e) {
//            throw new ServiceExecutionException("Could not persist states persistence file!", e);
//        }
    }
}
