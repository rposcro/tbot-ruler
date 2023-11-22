package com.tbot.ruler.console.views.actuators;

import com.tbot.ruler.console.accessors.RouteActuatorsAccessor;
import com.tbot.ruler.console.accessors.RoutePluginsAccessor;
import com.tbot.ruler.console.accessors.RouteThingsAccessor;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static com.tbot.ruler.console.utils.StreamUtils.toMap;

@RouteScope
@SpringComponent
public class ActuatorDataSupport {

    @Autowired
    private RouteActuatorsAccessor actuatorsAccessor;

    @Autowired
    private RouteThingsAccessor thingsAccessor;

    @Autowired
    private RoutePluginsAccessor pluginsAccessor;

    public List<ActuatorModel> getAllActuatorsModels() {
        Map<String, PluginResponse> pluginsMap = toMap(pluginsAccessor.getAllPlugins(), PluginResponse::getPluginUuid);
        Map<String, ThingResponse> thingsMap = toMap(thingsAccessor.getAllThings(), ThingResponse::getThingUuid);

        return actuatorsAccessor.getAllActuators().stream()
                .map(actuatorResponse -> ActuatorModel.builder()
                        .actuator(actuatorResponse)
                        .plugin(pluginsMap.get(actuatorResponse.getPluginUuid()))
                        .thing(thingsMap.get(actuatorResponse.getThingUuid()))
                        .build())
                .toList();
    }
}
