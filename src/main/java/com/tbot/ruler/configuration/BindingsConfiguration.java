package com.tbot.ruler.configuration;

import com.tbot.ruler.things.ActuatorId;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tbot.ruler.appliances.ApplianceBindings;
import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.things.CollectorId;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BindingsConfiguration {

    @Value("${ruler.thingsConfig.path}")
    private String configPath;

    @Autowired
    private DTOConfiguration dtoConfiguration;

    @Bean
    public List<ApplianceBindings> appliancesBindings() {
        List<ApplianceBindings> allBindings = dtoConfiguration.bindingDTOs().stream()
            .map(this::fromBindingDTO)
            .collect(Collectors.toList());
        log.debug(String.format("Appliances bindings read: %s", allBindings));
        return allBindings;
    }

    @Bean Map<ApplianceId, ApplianceBindings> applianceToBindingsMap() {
        return appliancesBindings().stream()
            .collect(Collectors.toMap(binding -> binding.getApplianceId(), Function.identity()));
    }

    @Bean
    public Map<EmitterId, Set<ApplianceId>> emittersToAppliancesMap() {
        Map<EmitterId, Set<ApplianceId>> theMap = new HashMap<>();
        appliancesBindings().forEach(bindings -> pivotBindingsByEmitter(bindings, theMap));
        return theMap;
    }

    @Bean
    public Map<ApplianceId, Set<CollectorId>> appliancesToCollectorsMap() {
        Map<ApplianceId, Set<CollectorId>> theMap = new HashMap<>();
        appliancesBindings().forEach(bindings -> {
            Set<CollectorId> collectorIdSet = theMap.computeIfAbsent(
                    bindings.getApplianceId(),
                    applianceId -> new HashSet());
            collectorIdSet.addAll(bindings.getCollectorIds());
        });
        return theMap;
    }

    @Bean
    public Map<ApplianceId, ActuatorId> applianceToActuatorMap() {
        Map<ApplianceId, ActuatorId> theMap = new HashMap<>();
        appliancesBindings().forEach(binding -> {
            if (binding.getActuatorId() != null) {
                theMap.put(binding.getApplianceId(), binding.getActuatorId());
            }
        });
        return theMap;
    }

    private void pivotBindingsByEmitter(ApplianceBindings bindings, Map<EmitterId, Set<ApplianceId>> theMap) {
        ApplianceId applianceId = bindings.getApplianceId();
        bindings.getEmitterIds().forEach(
            emitterId -> {
                theMap.computeIfAbsent(emitterId, id -> new HashSet<>()).add(applianceId);
            }
        );
    }

    private ApplianceBindings fromBindingDTO(BindingDTO bindingDTO) {
        return ApplianceBindings.builder()
            .applianceId(bindingDTO.getApplianceId())
            .actuatorId(bindingDTO.getActuatorId())
            .emitterIds(nullAsEmpty(bindingDTO.getEmitterIds()))
            .collectorIds(nullAsEmpty(bindingDTO.getCollectorIds()))
            .build();
    }

    private <T> List<T> nullAsEmpty(List<T> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return source;
    }
}
