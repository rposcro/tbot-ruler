package com.tbot.ruler.subjects.thing;

import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.Subject;

import java.util.List;

public interface Thing extends Subject {

    RulerThingContext getRulerThingContext();
    List<? extends Actuator> getActuators();
}
