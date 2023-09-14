package com.tbot.ruler.persistance.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.ServiceExecutionException;
import com.tbot.ruler.persistance.model.ApplianceState;
import com.tbot.ruler.persistance.ApplianceStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

@Service
public class JsonFileApplianceStateRepository implements ApplianceStateRepository {

    private ObjectMapper objectMapper;
    private Path statesFile;

    private HashMap<String, Class<?>> valueClassMap;
    private HashMap<String, ApplianceState> statesMap;

    @Autowired
    public JsonFileApplianceStateRepository(
            @Value("${ruler.cache.states.path}") String statesPath,
            ObjectMapper objectMapper) {
        this.statesFile = Paths.get(statesPath);
        this.objectMapper = objectMapper;
        this.valueClassMap = new HashMap<>();
    }

    @PostConstruct
    public void initialize() {
        if (!Files.exists(statesFile)) {
            statesMap = new HashMap<>();
            persistStatesTree();
        } else {
            statesMap = readStatesTree();
        }
    }

    @Override
    public ApplianceState save(ApplianceState applianceState) {
        statesMap.put(applianceState.getKey(), applianceState);
        persistStatesTree();
        return applianceState;
    }

    @Override
    public Optional<ApplianceState> findByKey(String applianceKey) {
        return Optional.ofNullable(statesMap.get(applianceKey));
    }

    public void flush() {
        persistStatesTree();
    }

    private HashMap<String, ApplianceState> readStatesTree() {
        try {
            HashMap<String, ApplianceState> statesMap = new HashMap<>();
            JsonNode rootNode = objectMapper.readTree(statesFile.toFile());
            rootNode.forEach(node -> {
                ApplianceState state = parseApplianceState(node);
                statesMap.put(state.getKey(), state);
            });
            return statesMap;
        } catch(IOException e) {
            throw new ServiceExecutionException("Could not read states from persistence file!", e);
        }
    }

    private ApplianceState parseApplianceState(JsonNode stateNode) {
        Class<?> stateClass = findStateClass(stateNode.get("valueClass").asText());
        Object stateValue = objectMapper.convertValue(stateNode.get("value"), stateClass);
        return ApplianceState.builder()
                .key(stateNode.get("key").asText())
                .valueClass(stateClass)
                .value(stateValue)
                .build();
    }

    private Class<?> findStateClass(String className) {
        try {
            Class<?> stateClass = valueClassMap.get(className);
            if (stateClass == null) {
                stateClass = this.getClass().getClassLoader().loadClass(className);
                valueClassMap.put(className, stateClass);
            }
            return stateClass;
        } catch(ClassNotFoundException e) {
            throw new ServiceExecutionException("Could not load value class " + className, e);
        }
    }

    private void persistStatesTree() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(statesFile.toFile(), statesMap);
        } catch(IOException e) {
            throw new ServiceExecutionException("Could not persist states persistence file!", e);
        }
    }
}
