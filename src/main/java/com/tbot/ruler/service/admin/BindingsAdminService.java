package com.tbot.ruler.service.admin;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.CollectorId;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BindingsAdminService {

    @Autowired
    private DTOConfiguration dtoConfiguration;

    public List<BindingDTO> allBindings() {
        return dtoConfiguration.bindingDTOs();
    }

    public List<ApplianceDTO> appliancesByEmitter(EmitterId emitterId) {
        return dtoConfiguration.bindingDTOs().stream()
            .filter(bindingDTO -> bindingDTO.getEmitterIds().contains(emitterId))
            .map(bindingDTO -> dtoConfiguration.applianceDTOMap().get(bindingDTO.getApplianceId()))
            .collect(Collectors.toList());
    }

    public List<ApplianceDTO> appliancesByCollector(CollectorId collectorId) {
        return dtoConfiguration.bindingDTOs().stream()
            .filter(bindingDTO -> bindingDTO.getCollectorIds().contains(collectorId))
            .map(bindingDTO -> dtoConfiguration.applianceDTOMap().get(bindingDTO.getApplianceId()))
            .collect(Collectors.toList());
    }

    public List<ApplianceDTO> appliancesByActuator(ActuatorId actuatorId) {
        return dtoConfiguration.bindingDTOs().stream()
            .filter(bindingDTO -> bindingDTO.getActuatorId().equals(actuatorId))
            .map(bindingDTO -> dtoConfiguration.applianceDTOMap().get(bindingDTO.getApplianceId()))
            .collect(Collectors.toList());
    }

    public List<EmitterDTO> bindedEmittersByAppliance(ApplianceId applianceId) {
        return dtoConfiguration.bindingDTOs().stream()
            .filter(bindingDTO -> applianceId.equals(bindingDTO.getApplianceId()))
            .flatMap(bindingDTO -> Optional.ofNullable(bindingDTO.getEmitterIds()).orElse(Collections.emptyList()).stream())
            .distinct()
            .map(emitterId -> dtoConfiguration.emitterDTOMap().get(emitterId))
            .collect(Collectors.toList());
    }

    public List<CollectorDTO> bindedCollectorsByAppliance(ApplianceId applianceId) {
        return dtoConfiguration.bindingDTOs().stream()
            .filter(bindingDTO -> bindingDTO.getApplianceId().equals(applianceId))
            .flatMap(bindingDTO -> Optional.ofNullable(bindingDTO.getCollectorIds()).orElse(Collections.emptyList()).stream())
            .distinct()
            .map(collectorId -> dtoConfiguration.collectorDTOMap().get(collectorId))
            .collect(Collectors.toList());
    }

    public ActuatorDTO bindedActuatorByAppliance(ApplianceId applianceId) {
        ActuatorId actuatorId = Optional.ofNullable(dtoConfiguration.applianceBindingDTOMap().get(applianceId))
            .orElse(null)
            .getActuatorId();
        return actuatorId == null ? null : dtoConfiguration.actuatorDTOMap().get(actuatorId);
    }
}
