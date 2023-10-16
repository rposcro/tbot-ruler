package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.Thing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Scope("singleton")
public class SubjectLifetimeService {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private PluginFactoryComponent pluginFactoryComponent;

    @Autowired
    private ThingFactoryComponent thingFactoryComponent;

    private List<Plugin> plugins;
    private Map<Long, Plugin> pluginsIdMap;
    private Map<String, Plugin> pluginsUuidMap;

    private List<Thing> things;
    private Map<Long, Thing> thingsIdMap;
    private Map<String, Thing> thingsUuidMap;

    private List<Actuator> actuators;
    private Map<String, Actuator> actuatorsUuidMap;

    @EventListener
    public void initialize(ApplicationStartedEvent event) {
        initializePlugins();
        initializeThings();
        initializeActuators();
    }

    public List<Plugin> getAllPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    public List<Thing> getAllThings() {
        return Collections.unmodifiableList(things);
    }

    public Thing getThingByUuid(String uuid) {
        return thingsUuidMap.get(uuid);
    }

    public List<Actuator> getAllActuators() {
        return Collections.unmodifiableList(actuators);
    }

    public Actuator getActuatorByUuid(String uuid) {
        return actuatorsUuidMap.get(uuid);
    }

    private void initializePlugins() {
        plugins = new LinkedList<>();
        pluginsIdMap = new HashMap<>();
        pluginsUuidMap = new HashMap<>();
        pluginsRepository.findAll().forEach(pluginEntity -> {
            Plugin plugin = pluginFactoryComponent.buildPlugin(pluginEntity);
            if (plugin != null) {
                plugins.add(plugin);
                pluginsIdMap.put(pluginEntity.getPluginId(), plugin);
                pluginsUuidMap.put(pluginEntity.getPluginUuid(), plugin);
            }
        });
    }

    private void initializeThings() {
        things = new LinkedList<>();
        thingsIdMap = new HashMap<>();
        thingsUuidMap = new HashMap<>();
        thingsRepository.findAll().forEach(thingEntity -> {
            Thing thing = thingFactoryComponent.buildThing(thingEntity);
            things.add(thing);
            thingsIdMap.put(thingEntity.getThingId(), thing);
            thingsUuidMap.put(thingEntity.getThingUuid(), thing);
        });
    }

    private void initializeActuators() {
        actuators = new LinkedList<>();
        actuatorsUuidMap = new HashMap<>();
        actuatorsRepository.findAll().forEach(actuatorEntity -> {
            Plugin plugin = pluginsIdMap.get(actuatorEntity.getPluginId());
            Thing thing = thingsIdMap.get(actuatorEntity.getThingId());
            Actuator actuator = plugin.startUpActuator(actuatorEntity, thing.getRulerThingContext());
            actuators.add(actuator);
            actuatorsUuidMap.put(actuator.getUuid(), actuator);
        });
    }
}
