package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CrudActuatorsRepository extends CrudRepository<ActuatorEntity, Long> {

    @Query("SELECT * FROM actuators WHERE thing_id = :thingId")
    Iterable<ActuatorEntity> findByThingId(long thingId);

    @Query("SELECT * FROM actuators WHERE plugin_id = :pluginId")
    Iterable<ActuatorEntity> findByPluginId(long pluginId);

    @Query("SELECT * FROM actuators WHERE actuator_uuid = :actuatorUuid")
    Optional<ActuatorEntity> findByUuid(String actuatorUuid);
}
