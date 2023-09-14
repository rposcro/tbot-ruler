package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.ThingEntity;

import java.util.List;

public interface ThingsRepository {

    List<ThingEntity> findByPluginUuid(String pluginUuid);
}
