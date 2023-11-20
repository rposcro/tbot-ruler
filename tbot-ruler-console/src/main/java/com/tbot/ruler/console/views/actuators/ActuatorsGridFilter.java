package com.tbot.ruler.console.views.actuators;

import lombok.Setter;

@Setter
public class ActuatorsGridFilter {

    private String nameTerm;
    private String referenceTerm;
    private String uuidTerm;
    private String pluginNameTerm;
    private String thingNameTerm;

    public boolean test(ActuatorModel actuator) {
        return matches(actuator.getName(), nameTerm)
                && matches(actuator.getReference(), referenceTerm)
                && matches(actuator.getActuatorUuid(), uuidTerm)
                && matches(actuator.getPluginName(), pluginNameTerm)
                && matches(actuator.getThingName(), thingNameTerm);
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
