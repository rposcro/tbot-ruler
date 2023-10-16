package com.tbot.ruler.subjects;

import com.tbot.ruler.subjects.thing.RulerThingContext;

import java.util.List;

public interface Thing extends Subject {

    RulerThingContext getRulerThingContext();
    List<? extends Actuator> getActuators();
}
