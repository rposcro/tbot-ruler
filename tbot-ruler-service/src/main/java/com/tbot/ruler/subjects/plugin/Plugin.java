package com.tbot.ruler.subjects.plugin;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.subjects.thing.RulerThingContext;

public interface Plugin extends Subject {

    Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext);
}
