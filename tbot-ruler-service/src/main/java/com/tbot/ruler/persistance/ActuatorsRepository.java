package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudActuatorsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ActuatorsRepository {

    @Autowired
    private CrudActuatorsRepository crudActuatorsRepository;

    public List<ActuatorEntity> findAll() {
        return StreamSupport.stream(crudActuatorsRepository.findAll().spliterator(), false)
                .toList();
    }

    public Optional<ActuatorEntity> findById(long actuatorId) {
        return crudActuatorsRepository.findById(actuatorId);
    }

    public Optional<ActuatorEntity> findByUuid(String actuatorUuid) {
        return crudActuatorsRepository.findByUuid(actuatorUuid);
    }

    public List<ActuatorEntity> findByThingId(long thingId) {
        return StreamSupport.stream(crudActuatorsRepository.findByThingId(thingId).spliterator(), false)
                .toList();
    }

    public boolean actuatorsForThingExist(long thingId) {
        return crudActuatorsRepository.countByThingId(thingId) > 0;
    }

    public boolean actuatorsForPluginExist(long pluginId) {
        return crudActuatorsRepository.countByPluginId(pluginId) > 0;
    }

    @Transactional
    public void delete(ActuatorEntity actuatorEntity) {
        crudActuatorsRepository.delete(actuatorEntity);
    }

    @Transactional
    public void deleteAll() {
        crudActuatorsRepository.deleteAll();
    }

    @Transactional
    public ActuatorEntity save(ActuatorEntity thingEntity) {
        return crudActuatorsRepository.save(thingEntity);
    }
}
