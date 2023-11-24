package com.tbot.ruler.console.views.routes.things;

import com.tbot.ruler.console.accessors.ActuatorsModelAccessor;
import com.tbot.ruler.console.views.routes.actuators.ActuatorsDialog;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@RouteScope
@SpringComponent
public class ThingActionSupport {

    @Autowired
    private ActuatorsModelAccessor actuatorsModelAccessor;

    public void openActuatorsDialog(ThingResponse thingResponse) {
        ActuatorsDialog.builder()
                .title("Actuators of thing " + thingResponse.getName())
                .actuators(actuatorsModelAccessor.getThingActuators(thingResponse.getThingUuid()))
                .build()
                .open();
    }
}
