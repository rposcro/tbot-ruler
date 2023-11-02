package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CrudThingsRepository extends CrudRepository<ThingEntity, Long> {

    @Query("SELECT * FROM things WHERE plugin_id = :pluginId")
    Iterable<ThingEntity> findByPluginId(long pluginId);

    @Query("SELECT * FROM things WHERE thing_uuid = :thingUuid")
    Optional<ThingEntity> findByUuid(String thingUuid);
}
