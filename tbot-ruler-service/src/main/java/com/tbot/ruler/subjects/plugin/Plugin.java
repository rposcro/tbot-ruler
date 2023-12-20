package com.tbot.ruler.subjects.plugin;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.subjects.thing.RulerThingContext;

public interface Plugin extends Subject {

    Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext);

    default void stopActuator(Actuator actuator, String reference) {
        throw new PluginException("Function not implemented for plugin " + this.getName());
    }
}
