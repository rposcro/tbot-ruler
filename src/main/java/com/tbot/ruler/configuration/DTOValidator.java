package com.tbot.ruler.configuration;

import com.tbot.ruler.exceptions.CriticalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DTOValidator {

    @Autowired
    private DTOConfiguration dtoConfiguration;

    @EventListener
    public void validateUniqueThingId(ApplicationReadyEvent event) {
        validateDistinctIds(
            "Things",
            dtoConfiguration.thingDTOs().stream().map(dto -> dto.getId()).collect(Collectors.toList()));
    }

    @EventListener
    public void validateUniqueEmitterId(ApplicationReadyEvent event) {
        validateDistinctIds(
            "Emitters",
            dtoConfiguration.emitterDTOs().stream()
                .map(dto -> dto.getId()).collect(Collectors.toList()));
    }

    @EventListener
    public void validateUniqueCollectorId(ApplicationReadyEvent event) {
        validateDistinctIds(
            "Collectors",
            dtoConfiguration.collectorDTOs().stream()
                .map(dto -> dto.getId()).collect(Collectors.toList()));
    }

    @EventListener
    public void validateUniqueActuatorId(ApplicationReadyEvent event) {
        validateDistinctIds(
            "Actuators",
            dtoConfiguration.actuatorDTOs().stream()
                .map(dto -> dto.getId()).collect(Collectors.toList()));
    }

    private void validateDistinctIds(String entityName, List<String> ids) {
        Set<String> idSet = new HashSet<>();
        ids.forEach(id -> {
            if (idSet.contains(id)) {
                throw new CriticalException(String.format("%s, duplicated ID: %s", entityName, id));
            } else {
                idSet.add(id);
            }
        });
    }
}
