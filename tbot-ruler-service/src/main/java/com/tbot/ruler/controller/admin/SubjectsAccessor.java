package com.tbot.ruler.controller.admin;

import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class SubjectsAccessor {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    public ActuatorEntity findActuator(String actuatorUuid) {
        return actuatorsRepository.findByUuid(actuatorUuid)
                .orElseThrow(() -> new ServiceRequestException(format("Actuator %s not found!", actuatorUuid)));
    }

    public ActuatorEntity findActuator(long actuatorId) {
        return actuatorsRepository.findById(actuatorId)
                .orElseThrow(() -> new ServiceRequestException(format("Actuator id %s not found!", actuatorId)));
    }

    public ThingEntity findThing(String thingUuid) {
        return thingsRepository.findByUuid(thingUuid)
                .orElseThrow(() -> new ServiceRequestException(format("Thing %s not found!", thingUuid)));
    }

    public ThingEntity findThing(long thingId) {
        return thingsRepository.findById(thingId)
                .orElseThrow(() -> new ServiceRequestException(format("Thing id %s not found!", thingId)));
    }

    public PluginEntity findPlugin(String pluginUuid) {
        return pluginsRepository.findByUuid(pluginUuid)
                .orElseThrow(() -> new ServiceRequestException(format("Plugin %s not found!", pluginUuid)));
    }

    public PluginEntity findPlugin(long pluginId) {
        return pluginsRepository.findById(pluginId)
                .orElseThrow(() -> new ServiceRequestException(format("Plugin id %s not found!", pluginId)));
    }
}
