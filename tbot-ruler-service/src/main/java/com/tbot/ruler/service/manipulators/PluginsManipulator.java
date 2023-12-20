package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginsManipulator {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    public void removePlugin(PluginEntity pluginEntity) {
        assertConsistency(pluginEntity);
        pluginsRepository.findByUuid(pluginEntity.getPluginUuid()).ifPresent(pluginsRepository::delete);
    }

    private void assertConsistency(PluginEntity pluginEntity) {
        if (actuatorsRepository.actuatorsForPluginExist(pluginEntity.getPluginId())) {
            throw new LifecycleException("Cannot remove plugin %s, actuator(s) exist(s)!", pluginEntity.getPluginUuid());
        }
    }
}
