package com.tbot.ruler.configuration;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.persistance.AppliancesRepository;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
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
    private AppliancesRepository appliancesRepository;

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
        return appliancesRepository.findAll().stream()
            .map(this::fromEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Bean
    public Map<String, Appliance> appliancesPerUuid() {
        return appliances().stream()
            .collect(Collectors.toMap(appliance -> appliance.getUuid(), Function.identity()));
    }

    private Optional<Appliance> fromEntity(ApplianceEntity entity) {
        try {
            Class<? extends Appliance> clazz = applianceClassesMap().get(entity.getApplianceType());
            if (clazz == null) {
                throw new NullPointerException("Appliance class " + entity.getApplianceType() + " could not be found!");
            }
            Constructor<? extends Appliance> constructor = clazz.getConstructor(String.class, ApplianceStatePersistenceService.class);
            if (constructor == null) {
                throw new NullPointerException("Appliance constructor for class " + entity.getApplianceType() + " could not be found!");
            }
            Appliance appliance = constructor.newInstance(entity.getApplianceUuid(), persistenceService);
            return Optional.of(appliance);
        } catch(ReflectiveOperationException | SecurityException e) {
            log.error("Incorrect appliance class type: " + entity.getApplianceType() + ", skipping appliance: " + entity.getApplianceUuid());
            return Optional.empty();
        }
    }
}
