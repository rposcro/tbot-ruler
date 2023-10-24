package com.tbot.ruler.plugins.agent.activator;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.thing.RulerThingAgent;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ThingActivationActuator extends AbstractActuator {

    private final ActuatorState<OnOffState> state;
    private final RulerThingContext rulerThingContext;

    @Builder
    public ThingActivationActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull RulerThingContext rulerThingContext) {
        super(uuid, name, description);
        this.rulerThingContext = rulerThingContext;
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .payload(OnOffState.of(rulerThingContext.getRulerThingAgent().isActivated()))
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        OnOffState requestedState = message.getPayloadAs(OnOffState.class);
        state.updatePayload(requestedState);
        rulerThingContext.getRulerThingAgent().setActivated(requestedState);
        log.info("Thing {} activation flag changed to {}", rulerThingContext.getThingUuid(), state.getPayload().isOn());
    }
}
