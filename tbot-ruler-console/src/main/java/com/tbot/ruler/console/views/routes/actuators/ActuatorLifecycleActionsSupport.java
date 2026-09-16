package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.ActuatorsAccessor;
import com.tbot.ruler.console.views.AbstractActionsSupport;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class ActuatorLifecycleActionsSupport extends AbstractActionsSupport {

    @Autowired
    private ActuatorsAccessor actuatorsAccessor;

    public boolean activateActuator(String actuatorUuid) {
        return handlingExceptions(() -> {
            actuatorsAccessor.activateActuator(actuatorUuid);
            notifyInfo("Actuator activated");
        });
    }

    public boolean deactivateActuator(String actuatorUuid) {
        return handlingExceptions(() -> {
            actuatorsAccessor.deactivateActuator(actuatorUuid);
            notifyInfo("Actuator deactivated");
        });
    }
}
