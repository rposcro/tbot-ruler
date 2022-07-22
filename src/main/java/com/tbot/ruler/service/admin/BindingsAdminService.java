package com.tbot.ruler.service.admin;

import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.ItemId;
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

    public List<EmitterDTO> sendingEmittersForItem(ItemId itemId) {
        List<EmitterDTO> emitters = dtoConfiguration.bindingDTOs().stream()
                .filter(binding -> binding.getConsumerIds().contains(itemId))
                .filter(binding -> binding.getSenderId() instanceof EmitterId)
                .map(binding -> (EmitterId) binding.getSenderId())
                .map(emitterId -> dtoConfiguration.emitterDTOMap().get(emitterId))
                .collect(Collectors.toList());
        return emitters;
    }

    public List<ActuatorDTO> sendingActuatorsForItem(ItemId itemId) {
        List<ActuatorDTO> actuators = dtoConfiguration.bindingDTOs().stream()
                .filter(binding -> binding.getConsumerIds().contains(itemId))
                .filter(binding -> binding.getSenderId() instanceof ActuatorId)
                .map(binding -> (ActuatorId) binding.getSenderId())
                .map(actuatorId -> dtoConfiguration.actuatorDTOMap().get(actuatorId))
                .collect(Collectors.toList());
        return actuators;
    }
}
