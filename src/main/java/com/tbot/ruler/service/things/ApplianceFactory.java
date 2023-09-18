package com.tbot.ruler.service.things;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import com.tbot.ruler.util.PackageScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ApplianceFactory {

    @Autowired
    private ApplianceStatePersistenceService persistenceService;

    private Map<String, Class<? extends Appliance>> applianceClassesMap;

    public ApplianceFactory() {
        PackageScanner scanner = new PackageScanner();
        Set<Class<? extends Appliance>> classes = scanner.findAllClassesOfType(Appliance.class, "com.tbot.ruler.appliances");
        this.applianceClassesMap = classes.stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName(), clazz -> clazz));
    }

    public List<Appliance> buildAppliances(List<ApplianceEntity> applianceEntities) {
        List<Appliance> appliances = new LinkedList<>();
        return applianceEntities.stream()
                .map(this::buildAppliance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Appliance> buildAppliance(ApplianceEntity entity) {
        try {
            Class<? extends Appliance> clazz = applianceClassesMap.get(entity.getApplianceType());
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
