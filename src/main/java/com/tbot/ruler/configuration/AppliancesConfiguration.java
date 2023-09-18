package com.tbot.ruler.configuration;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import com.tbot.ruler.persistance.json.dto.ApplianceDTO;
import com.tbot.ruler.util.PackageScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AppliancesConfiguration {

    @Autowired
    private List<ApplianceDTO> applianceDTOList;

    @Autowired
    ApplianceStatePersistenceService persistenceService;

    @Bean
    public Map<String, Class<? extends Appliance>> applianceClassesMap() {
        PackageScanner scanner = new PackageScanner();
        Set<Class<? extends Appliance>> classes = scanner.findAllClassesOfType(Appliance.class, "com.tbot.ruler.appliances");
        return classes.stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName(), clazz -> clazz));
    }

    @Bean
    public List<Appliance> appliances() {
        return applianceDTOList.stream()
            .map(this::fromDTO)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Bean
    public Map<String, Appliance> appliancesPerId() {
        return appliances().stream()
            .collect(Collectors.toMap(appliance -> appliance.getUuid(), Function.identity()));
    }

    private Optional<Appliance> fromDTO(ApplianceDTO dto) {
        try {
            Class<? extends Appliance> clazz = applianceClassesMap().get(dto.getType());
            if (clazz == null) {
                throw new NullPointerException("Appliance class " + dto.getType() + " could not be found!");
            }
            Constructor<? extends Appliance> constructor = clazz.getConstructor(String.class, ApplianceStatePersistenceService.class);
            if (constructor == null) {
                throw new NullPointerException("Appliance constructor for class " + dto.getType() + " could not be found!");
            }
            Appliance appliance = constructor.newInstance(dto.getUuid(), persistenceService);
            return Optional.of(appliance);
        } catch(ReflectiveOperationException | SecurityException e) {
            log.error("Incorrect appliance class type: " + dto.getType() + ", skipping appliance: " + dto.getUuid());
            return Optional.empty();
        }
    }
}
