package com.tbot.ruler.plugins.ghost.activator;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.thing.RulerThingAgent;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GhostThingActivationActuator extends AbstractActuator {

    private final ActuatorState<OnOffState> state;
    private final RulerThingAgent rulerThingAgent;

    @Builder
    public GhostThingActivationActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull RulerThingAgent rulerThingAgent) {
        super(uuid, name, description);
        this.rulerThingAgent = rulerThingAgent;
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .payload(OnOffState.of(rulerThingAgent.isActivated()))
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        OnOffState requestedState = message.getPayloadAs(OnOffState.class);
        state.updatePayload(requestedState);
        rulerThingAgent.setActivated(requestedState);
        log.info("Ghost actuator {} activation flag changed to {}", this.getUuid(), state.getPayload().isOn());
    }
}
