package com.tbot.ruler.subjects.thing;

import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@Getter
public final class RulerThing extends AbstractSubject implements Thing {

    private final RulerThingContext rulerThingContext;
    private final List<Actuator> actuators;

    @Builder
    public RulerThing(@NonNull RulerThingContext rulerThingContext) {
        super(rulerThingContext.getThingUuid(), rulerThingContext.getThingName(), null);
        this.rulerThingContext = rulerThingContext;
        this.actuators = new LinkedList<>();
        this.rulerThingContext.getRulerThingAgent().setThing(this);
    }

    public void addActuator(Actuator actuator) {
        actuators.add(actuator);
    }

    public void removeActuator(Actuator actuator) {
        actuators.remove(actuator);
    }
}
