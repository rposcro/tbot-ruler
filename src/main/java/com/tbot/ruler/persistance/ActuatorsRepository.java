package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.ActuatorEntity;

import java.util.List;

public interface ActuatorsRepository {

    List<ActuatorEntity> findByThingUuid(String thingUuid);
}
