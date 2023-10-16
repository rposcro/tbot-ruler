package com.tbot.ruler.plugins.ghost.activator;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.ghost.GhostThingAgent;
import com.tbot.ruler.subjects.AbstractActuator;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GhostThingActivationActuator extends AbstractActuator {

    private final ActuatorState<OnOffState> state;
    private final GhostThingAgent ghostThingAgent;

    @Builder
    public GhostThingActivationActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull GhostThingAgent ghostThingAgent) {
        super(uuid, name, description);
        this.ghostThingAgent = ghostThingAgent;
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .payload(OnOffState.of(ghostThingAgent.isActivated()))
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        OnOffState requestedState = message.getPayloadAs(OnOffState.class);
        state.updatePayload(requestedState);
        ghostThingAgent.setActivated(requestedState);
        log.info("Ghost actuator {} activation flag changed to {}", this.getUuid(), state.getPayload().isOn());
    }
}
