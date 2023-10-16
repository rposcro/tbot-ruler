package com.tbot.ruler.subjects.thing;

import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.Thing;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@Getter
public class RulerThing extends AbstractSubject implements Thing {

    private final RulerThingContext rulerThingContext;
    private final List<Actuator> actuators;

    @Builder
    public RulerThing(@NonNull RulerThingContext rulerThingContext) {
        super(rulerThingContext.getThingUuid(), rulerThingContext.getThingName(), null);
        this.rulerThingContext = rulerThingContext;
        this.actuators = new LinkedList<>();
    }

    public void addActuator(Actuator actuator) {
        this.actuators.add(actuator);
    }
}
