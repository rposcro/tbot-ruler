package com.tbot.ruler.service.admin;

import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThingsAdminService {

    @Autowired
    private DTOConfiguration dtoConfiguration;

    public List<ThingDTO> allThings() {
        return dtoConfiguration.thingDTOs();
    }

    public ThingDTO thingDTOById(String thingId) {
        return dtoConfiguration.thingDTOMap().get(thingId);
    }

    public Map<String, List<ThingDTO>> thingsByPlugin() {
        return dtoConfiguration.thingDTOs().stream()
            .collect(Collectors.groupingBy(ThingDTO::getPluginAlias));
    }

    public List<EmitterDTO> allEmitters() {
        return dtoConfiguration.emitterDTOs();
    }

    public EmitterDTO emitterDTOById(String emitterId) {
        return dtoConfiguration.emitterDTOMap().get(emitterId);
    }

    public List<CollectorDTO> allCollectors() {
        return dtoConfiguration.collectorDTOs();
    }

    public CollectorDTO collectorDTOById(String collectorId) {
        return dtoConfiguration.collectorDTOMap().get(collectorId);
    }

    public List<ActuatorDTO> allActuators() {
        return dtoConfiguration.actuatorDTOs();
    }

    public ActuatorDTO actuatorDTOById(String actuatorId) {
        return dtoConfiguration.actuatorDTOMap().get(actuatorId);
    }
}
