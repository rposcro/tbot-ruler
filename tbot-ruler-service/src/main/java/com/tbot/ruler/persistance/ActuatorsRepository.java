package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudActuatorsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActuatorsRepository {

    @Autowired
    private CrudActuatorsRepository crudActuatorsRepository;

    public List<ActuatorEntity> findAll() {
        List<ActuatorEntity> entities = StreamSupport.stream(crudActuatorsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return entities;
    }

    public Optional<ActuatorEntity> findById(long actuatorId) {
        Optional<ActuatorEntity> actuatorEntity = crudActuatorsRepository.findById(actuatorId);
        return actuatorEntity;
    }

    public Optional<ActuatorEntity> findByUuid(String actuatorUuid) {
        Optional<ActuatorEntity> actuatorEntity = crudActuatorsRepository.findByUuid(actuatorUuid);
        return actuatorEntity;
    }

    public List<ActuatorEntity> findByThingId(long thingId) {
        List<ActuatorEntity> entities = StreamSupport.stream(crudActuatorsRepository.findByThingId(thingId).spliterator(), false)
                .collect(Collectors.toList());
        return entities;
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
    public ActuatorEntity save(ActuatorEntity thingEntity) {
        return crudActuatorsRepository.save(thingEntity);
    }
}
