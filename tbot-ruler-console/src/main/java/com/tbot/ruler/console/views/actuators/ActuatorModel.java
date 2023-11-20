package com.tbot.ruler.console.views.actuators;

import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@Builder
public class ActuatorModel {

    @Delegate
    private ActuatorResponse actuator;

    private PluginResponse plugin;

    private ThingResponse thing;

    public String getPluginName() {
        return plugin != null ? plugin.getName() : null;
    }

    public String getThingName() {
        return thing != null ? thing.getName() : null;
    }
}
