package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.ActuatorEntity;

import java.util.List;
import java.util.Optional;

public interface ActuatorsRepository {

    List<ActuatorEntity> findAll();

    List<ActuatorEntity> findByThingUuid(String thingUuid);

    Optional<ActuatorEntity> findByUuid(String actuatorUuid);
}
