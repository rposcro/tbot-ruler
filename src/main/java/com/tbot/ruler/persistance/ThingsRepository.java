package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudActuatorsRepository;
import com.tbot.ruler.persistance.jdbc.CrudThingsRepository;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .collect(Collectors.toList());
        return entities;
    }

    public Optional<ThingEntity> findByUuid(String thingUuid) {
        Optional<ThingEntity> thingEntity = crudThingsRepository.findByUuid(thingUuid);
        return thingEntity;
    }

    @Transactional
    public void delete(ThingEntity thingEntity) {
        crudThingsRepository.delete(thingEntity);
    }

    @Transactional
    public ThingEntity save(ThingEntity thingEntity) {
        ThingEntity savedThingEntity = crudThingsRepository.save(thingEntity);
        return savedThingEntity;
    }
}
