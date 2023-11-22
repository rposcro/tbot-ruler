package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.views.components.GridFilter;
import lombok.Setter;

@Setter
public class ActuatorsGridFilter implements GridFilter<ActuatorModel> {

    private String nameTerm;
    private String referenceTerm;
    private String uuidTerm;
    private String pluginNameTerm;
    private String thingNameTerm;

    @Override
    public boolean test(ActuatorModel actuator) {
        return matches(actuator.getName(), nameTerm)
                && matches(actuator.getReference(), referenceTerm)
                && matches(actuator.getActuatorUuid(), uuidTerm)
                && matches(actuator.getPluginName(), pluginNameTerm)
                && matches(actuator.getThingName(), thingNameTerm);
    }
}
