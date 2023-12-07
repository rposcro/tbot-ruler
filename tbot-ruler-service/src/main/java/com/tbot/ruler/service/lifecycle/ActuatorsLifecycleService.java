package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.plugins.Plugin;
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

    private List<Actuator> actuators;
    private Map<String, Actuator> actuatorsUuidMap;

    public List<Actuator> getAllActuators() {
        return Collections.unmodifiableList(actuators);
    }

    public Actuator getActuatorByUuid(String uuid) {
        return actuatorsUuidMap.get(uuid);
    }

    public void startUpAllActuators() {
        actuators = new LinkedList<>();
        actuatorsUuidMap = new HashMap<>();
        actuatorsRepository.findAll().forEach(actuatorEntity -> {
            try {
                Plugin plugin = pluginsLifecycleService.getPluginById(actuatorEntity.getPluginId());
                RulerThing thing = thingsLifecycleService.getThingById(actuatorEntity.getThingId());
                Actuator actuator = plugin.startUpActuator(actuatorEntity, thing.getRulerThingContext());
                thing.addActuator(actuator);
                actuators.add(actuator);
                actuatorsUuidMap.put(actuator.getUuid(), actuator);
            } catch(PluginException e) {
                log.error("Failed to start up actuator " + actuatorEntity.getActuatorUuid(), e);
            }
        });
    }

}
