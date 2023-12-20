package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.views.components.EntityFilterableGrid;

public class ActuatorsGrid extends EntityFilterableGrid<ActuatorModel> {

    public ActuatorsGrid() {
        super(ActuatorModel.class, new String[] {"name", "reference", "actuatorUuid", "pluginName", "thingName"});
    }
}
