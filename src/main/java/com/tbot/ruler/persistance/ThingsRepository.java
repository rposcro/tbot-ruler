package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudActuatorsRepository;
import com.tbot.ruler.persistance.jdbc.CrudThingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ThingsRepository {

    @Autowired
    private CrudThingsRepository crudThingsRepository;

    @Autowired
    private CrudActuatorsRepository crudActuatorsRepository;

    public List<ThingEntity> findAll() {
        List<ThingEntity> entities = StreamSupport.stream(crudThingsRepository.findAll().spliterator(), false)
                .peek(this::fillThingEntity)
                .collect(Collectors.toList());
        return entities;
    }

    public Optional<ThingEntity> findByUuid(String thingUuid) {
        Optional<ThingEntity> thingEntity = crudThingsRepository.findByUuid(thingUuid);
        thingEntity.ifPresent(this::fillThingEntity);
        return thingEntity;
    }

    @Transactional
    public void delete(ThingEntity thingEntity) {
        crudThingsRepository.delete(thingEntity);
    }

    @Transactional
    public ThingEntity save(ThingEntity thingEntity) {
        ThingEntity savedThingEntity = crudThingsRepository.save(thingEntity);

        thingEntity.getActuators().stream().forEach(actuatorEntity -> {
            actuatorEntity.setThingId(savedThingEntity.getThingId());
        });

        List<ActuatorEntity> savedActuators = StreamSupport.stream(crudActuatorsRepository.saveAll(thingEntity.getActuators()).spliterator(), false)
                .collect(Collectors.toList());
        savedThingEntity.setActuators(savedActuators);

        return savedThingEntity;
    }

    private void fillThingEntity(ThingEntity thingEntity) {
        List<ActuatorEntity> actuators = new LinkedList<>();
        crudActuatorsRepository.findByThingId(thingEntity.getThingId()).forEach(actuators::add);
        thingEntity.setActuators(actuators);
    }
}
