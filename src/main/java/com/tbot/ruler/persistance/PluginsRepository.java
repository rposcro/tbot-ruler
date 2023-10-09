package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudActuatorsRepository;
import com.tbot.ruler.persistance.jdbc.CrudPluginsRepository;
import com.tbot.ruler.persistance.jdbc.CrudThingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PluginsRepository {

    @Autowired
    private CrudPluginsRepository crudPluginsRepository;

    @Autowired
    private CrudThingsRepository crudThingsRepository;

    @Autowired
    private CrudActuatorsRepository crudActuatorsRepository;

    public List<PluginEntity> findAll() {
        List<PluginEntity> entities = StreamSupport.stream(crudPluginsRepository.findAll().spliterator(), false)
                .peek(this::fillPluginEntity)
                .collect(Collectors.toList());
        return entities;
    }

    public Optional<PluginEntity> findByUuid(String pluginUuid) {
        Optional<PluginEntity> pluginEntity = crudPluginsRepository.findByUuid(pluginUuid);
        if (pluginEntity.isPresent()) {
            fillPluginEntity(pluginEntity.get());
        }
        return pluginEntity;
    }

    @Transactional
    public void delete(PluginEntity pluginEntity) {
        crudPluginsRepository.delete(pluginEntity);
    }

    @Transactional
    public PluginEntity save(PluginEntity pluginEntity) {
        PluginEntity savedPlugin = crudPluginsRepository.save(pluginEntity);

        pluginEntity.getThings().stream().forEach(thingEntity -> {
            thingEntity.setPluginId(savedPlugin.getPluginId());
        });
        Map<Long, ThingEntity> savedThingsMap = StreamSupport.stream(crudThingsRepository.saveAll(pluginEntity.getThings()).spliterator(), false)
                        .collect(Collectors.toMap(ThingEntity::getThingId, Function.identity()));

        pluginEntity.getThings().forEach(thingEntity -> {
            ThingEntity savedEntity = savedThingsMap.get(thingEntity.getThingId());
            thingEntity.getActuators().forEach(actuatorEntity -> {
                actuatorEntity.setThingId(savedEntity.getThingId());
            });
            List<ActuatorEntity> savedActuators = StreamSupport.stream(crudActuatorsRepository.saveAll(thingEntity.getActuators()).spliterator(), false)
                    .collect(Collectors.toList());
            savedEntity.setActuators(savedActuators);
        });
        savedPlugin.setThings(savedThingsMap.values().stream().collect(Collectors.toList()));
        return savedPlugin;
    }

    private void fillPluginEntity(PluginEntity pluginEntity) {
        List<ThingEntity> things = new LinkedList<>();
        crudThingsRepository.findByPluginId(pluginEntity.getPluginId()).forEach(thingEntity -> {
            List<ActuatorEntity> actuators = new LinkedList<>();
            crudActuatorsRepository.findByThingId(thingEntity.getThingId()).forEach(actuators::add);
            thingEntity.setActuators(actuators);
            things.add(thingEntity);
        });
        pluginEntity.setThings(things);
    }
}
