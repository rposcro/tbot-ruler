package com.tbot.ruler.service.things;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.things.Actuator;
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
public class ThingsLifetimeService {

    @Autowired
    private PluginsRepository pluginsRepository;
    @Autowired
    private PluginFactory pluginFactory;

    private List<Plugin> plugins;
    private Map<String, Actuator> actuatorsMap;

    @PostConstruct
    public void init() {
        plugins = pluginFactory.buildPlugins(pluginsRepository.findAll());
        actuatorsMap = plugins.stream()
                .flatMap(plugin -> plugin.getThings().stream())
                .flatMap(thing -> thing.getActuators().stream())
                .collect(Collectors.toMap(Actuator::getUuid, Function.identity()));
    }

    public List<Plugin> getAllPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    public Actuator getActuatorByUuid(String uuid) {
        return actuatorsMap.get(uuid);
    }
}
