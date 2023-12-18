package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ActuatorsLifecycleService {

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @Autowired
    private ThingsLifecycleService thingsLifecycleService;

    @Autowired
    private JobsLifecycleService jobsLifecycleService;

    private List<Actuator> actuators;
    private Map<String, Actuator> actuatorsUuidMap;
    private Map<String, RulerThing> thingsByActuatorsUuidMap;
    private Map<String, Plugin> pluginsByActuatorsUuidMap;

    public List<Actuator> getAllActuators() {
        return Collections.unmodifiableList(actuators);
    }

    public Actuator getActuatorByUuid(String uuid) {
        return actuatorsUuidMap.get(uuid);
    }

    public void deactivateActuator(ActuatorEntity actuatorEntity) {
        String actuatorUuid = actuatorEntity.getActuatorUuid();

        if (actuatorsUuidMap.containsKey(actuatorUuid)) {
            log.warn("Actuators' Lifecycle: Deactivation skipped, actuator is not active {} {}",
                    actuatorEntity.getActuatorUuid(), actuatorEntity.getName());
            return;
        }

        Actuator actuator = actuatorsUuidMap.get(actuatorUuid);
        RulerThing thing = thingsByActuatorsUuidMap.get(actuatorUuid);
        Plugin plugin = pluginsByActuatorsUuidMap.get(actuatorUuid);

        jobsLifecycleService.stopSubjectJobs(actuator);
        thing.removeActuator(actuator);
        plugin.stopActuator(actuator, actuatorEntity.getReference());

        actuatorsUuidMap.remove(actuatorUuid);
        thingsByActuatorsUuidMap.remove(actuatorUuid);
        pluginsByActuatorsUuidMap.remove(actuatorUuid);
        actuators.remove(actuator);

        log.info("Actuators' Lifecycle: Actuator deactivated {} {}", actuator.getUuid(), actuator.getName());
    }

    public void activateAllActuators() {
        actuators = new LinkedList<>();
        actuatorsUuidMap = new HashMap<>();
        thingsByActuatorsUuidMap = new HashMap<>();
        pluginsByActuatorsUuidMap = new HashMap<>();

        actuatorsRepository.findAll().forEach(actuatorEntity -> {
            try {
                activateActuator(actuatorEntity);
            } catch(PluginException e) {
                log.error("Actuators' Lifecycle: Failed to start up actuator " + actuatorEntity.getActuatorUuid(), e);
            }
        });
    }

    public void activateActuator(ActuatorEntity actuatorEntity) {
        if (actuatorsUuidMap.containsKey(actuatorEntity.getActuatorUuid())) {
            log.warn("Actuators' Lifecycle: Activation skipped, actuator is already active {} {}",
                    actuatorEntity.getActuatorUuid(), actuatorEntity.getName());
            return;
        }

        Plugin plugin = pluginsLifecycleService.getPluginById(actuatorEntity.getPluginId());
        RulerThing thing = thingsLifecycleService.getThingById(actuatorEntity.getThingId());
        Actuator actuator = plugin.startUpActuator(actuatorEntity, thing.getRulerThingContext());
        thing.addActuator(actuator);
        jobsLifecycleService.startSubjectJobs(actuator);

        actuators.add(actuator);
        actuatorsUuidMap.put(actuator.getUuid(), actuator);
        thingsByActuatorsUuidMap.put(actuator.getUuid(), thing);
        pluginsByActuatorsUuidMap.put(actuator.getUuid(), plugin);

        log.info("Actuators' Lifecycle: Actuator activated {} {}", actuator.getUuid(), actuator.getName());
    }

    public boolean isActuatorActive(String actuatorUuid) {
        return actuatorsUuidMap.containsKey(actuatorUuid);
    }
}
