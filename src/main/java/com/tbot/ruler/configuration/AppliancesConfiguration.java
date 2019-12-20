package com.tbot.ruler.configuration;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceClass;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
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
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AppliancesConfiguration {

    @Autowired
    private List<ApplianceDTO> applianceDTOList;

    @Bean
    public Map<String, Class<? extends Appliance>> applianceClassesMap() {
        PackageScanner scanner = new PackageScanner();
        Set<Class<? extends Appliance>> classes = scanner.findAllClassifiedClasses(ApplianceClass.class, Appliance.class, "com.tbot.ruler.appliances");
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
    public Map<ApplianceId, Appliance> appliancesPerId() {
        return appliances().stream()
            .collect(Collectors.toMap(Appliance::getId, app -> app));
    }

    private Optional<Appliance> fromDTO(ApplianceDTO dto) {
        try {
            Class<? extends Appliance> clazz = applianceClassesMap().get(dto.getType());
            Constructor<? extends Appliance> constructor = clazz.getConstructor(ApplianceId.class);
            Appliance appliance = constructor.newInstance(dto.getId());
            appliance.setName(dto.getName());
            appliance.setDescription(dto.getDescription());
            return Optional.of(appliance);
        } catch(ReflectiveOperationException | SecurityException e) {
            log.error("Incorrect appliance class type: " + dto.getType() + ", skipping appliance: " + dto.getId());
            return Optional.empty();
        }
    }
}
