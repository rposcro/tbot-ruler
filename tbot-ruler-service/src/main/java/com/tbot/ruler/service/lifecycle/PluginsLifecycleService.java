package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.plugins.Plugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Scope("singleton")
public class PluginsLifecycleService {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private PluginFactoryComponent pluginFactoryComponent;

    private List<Plugin> plugins;
    private Map<Long, Plugin> pluginsIdMap;
    private Map<String, Plugin> pluginsUuidMap;

    public List<Plugin> getAllPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    public Plugin getPluginById(long id) {
        return pluginsIdMap.get(id);
    }

    public void startUpAllPlugins() {
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
}
