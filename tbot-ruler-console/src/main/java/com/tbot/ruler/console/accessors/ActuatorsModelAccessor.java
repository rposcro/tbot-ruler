package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@RouteScope
@SpringComponent
public class ActuatorsModelAccessor {

    @Autowired
    private ActuatorsAccessor actuatorsAccessor;

    @Autowired
    private ThingsAccessor thingsAccessor;

    @Autowired
    private PluginsAccessor pluginsAccessor;

    public List<ActuatorModel> getAllActuatorsModels() {
        return toModels(actuatorsAccessor.getAllActuators());
    }

    public List<ActuatorModel> getThingActuators(String thingUuid) {
        List<ActuatorResponse> actuators = actuatorsAccessor.getAllActuators();
        actuators.removeIf(actuator -> !thingUuid.equals(actuator.getThingUuid()));
        return toModels(actuators);
    }

    private List<ActuatorModel> toModels(List<ActuatorResponse> actuators) {
        Map<String, PluginResponse> pluginsMap = pluginsAccessor.getPluginsUuidMap();
        Map<String, ThingResponse> thingsMap = thingsAccessor.getThingsUuidMap();

        return actuators.stream()
                .map(actuatorResponse -> ActuatorModel.builder()
                        .actuator(actuatorResponse)
                        .plugin(pluginsMap.get(actuatorResponse.getPluginUuid()))
                        .thing(thingsMap.get(actuatorResponse.getThingUuid()))
                        .build())
                .toList();
    }
}
