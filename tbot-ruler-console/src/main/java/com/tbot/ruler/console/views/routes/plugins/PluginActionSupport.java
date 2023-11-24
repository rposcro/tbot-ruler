package com.tbot.ruler.console.views.routes.plugins;

import com.tbot.ruler.console.accessors.ActuatorsModelAccessor;
import com.tbot.ruler.console.views.routes.actuators.ActuatorsDialog;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@RouteScope
@SpringComponent
public class PluginActionSupport {

    @Autowired
    private ActuatorsModelAccessor actuatorsModelAccessor;

    public void openActuatorsDialog(PluginResponse pluginResponse) {
        ActuatorsDialog.builder()
                .title("Actuators of plugin " + pluginResponse.getName())
                .actuators(actuatorsModelAccessor.getPluginActuators(pluginResponse.getPluginUuid()))
                .build()
                .open();
    }
}
