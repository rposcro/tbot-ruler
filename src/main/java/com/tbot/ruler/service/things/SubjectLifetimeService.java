package com.tbot.ruler.service.things;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.Thing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope("singleton")
public class SubjectLifetimeService {

    @Autowired
    private PluginsRepository pluginsRepository;
    @Autowired
    private PluginFactory pluginFactory;

    private List<Plugin> plugins;
    private List<Thing> things;
    private List<Actuator> actuators;
    private Map<String, Actuator> actuatorsMap;

    @PostConstruct
    public void init() {
        plugins = pluginFactory.buildPlugins(pluginsRepository.findAll());
        things = plugins.stream()
                .flatMap(plugin -> plugin.getThings().stream())
                .collect(Collectors.toList());
        actuators = things.stream()
                .flatMap(thing -> thing.getActuators().stream())
                .collect(Collectors.toList());
        actuatorsMap = actuators.stream()
                .collect(Collectors.toMap(Actuator::getUuid, Function.identity()));
    }

    public List<Plugin> getAllPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    public List<Thing> getAllThings() {
        return Collections.unmodifiableList(things);
    }

    public List<Actuator> getAllActuators() {
        return Collections.unmodifiableList(actuators);
    }

    public Actuator getActuatorByUuid(String uuid) {
        return actuatorsMap.get(uuid);
    }
}
