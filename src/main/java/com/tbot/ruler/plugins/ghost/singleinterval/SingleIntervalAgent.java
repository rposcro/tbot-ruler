package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.Getter;

@Getter
public class SingleIntervalAgent {

    private ActuatorState<OnOffState> currentState;

    public SingleIntervalAgent(String actuatorUuid, boolean defaultState) {
        this.currentState = ActuatorState.<OnOffState>builder()
                .actuatorUuid(actuatorUuid)
                .payload(OnOffState.of(defaultState))
                .build();
    }

    public boolean isActive() {
        return currentState.getPayload().isOn();
    }

    public void setActive(boolean active) {
        currentState.updatePayload(OnOffState.of(active));
    }
}
