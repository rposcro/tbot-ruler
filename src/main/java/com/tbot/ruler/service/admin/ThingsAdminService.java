package com.tbot.ruler.service.admin;

import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThingsAdminService {

    @Autowired
    private ThingsRepository thingsRepository;
    @Autowired
    private ActuatorsRepository actuatorsRepository;

    public List<ThingEntity> allThings() {
        return thingsRepository.findAll();
    }

    public ThingEntity thingByUuid(String thingUuid) {
        return thingsRepository.findByUuid(thingUuid).orElse(null);
    }

    public List<ActuatorEntity> allActuators() {
        return actuatorsRepository.findAll();
    }

    public ActuatorEntity actuatorByUuid(String actuatorUuid) {
        return actuatorsRepository.findByUuid(actuatorUuid).orElse(null);
    }
}
