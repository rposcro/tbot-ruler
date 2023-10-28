package com.tbot.ruler.plugins.agent.trigger;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.broker.payload.Trigger;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ThingTriggerActuator extends AbstractActuator {

    private final ActuatorState<OnOffState> state;
    private final RulerThingContext thingContext;

    @Builder
    public ThingTriggerActuator(
            @NonNull ActuatorEntity actuatorEntity,
            @NonNull RulerThingContext thingContext) {
        super(actuatorEntity.getActuatorUuid(), actuatorEntity.getName(), actuatorEntity.getDescription());
        this.thingContext = thingContext;
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .payload(OnOffState.STATE_ON)
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        message.getPayloadAs(Trigger.class);
        thingContext.getRulerThingAgent().triggerActuators();
        log.info("Actuators on thing {} triggered", thingContext.getThingUuid());
    }
}
