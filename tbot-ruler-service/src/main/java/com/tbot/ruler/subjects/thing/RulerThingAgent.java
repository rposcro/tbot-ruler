package com.tbot.ruler.subjects.thing;

import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class RulerThingAgent {

    @Getter
    @Setter
    private boolean onMute = false;

    @Setter(AccessLevel.PROTECTED)
    private RulerThing thing;

    public void triggerActuators() {
        thing.getActuators().forEach(Actuator::triggerAction);
    }
}
