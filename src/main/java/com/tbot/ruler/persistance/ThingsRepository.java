package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.ThingEntity;

import java.util.List;
import java.util.Optional;

public interface ThingsRepository {

    List<ThingEntity> findAll();

    List<ThingEntity> findByPluginUuid(String pluginUuid);

    Optional<ThingEntity> findByUuid(String thingUuid);
}
