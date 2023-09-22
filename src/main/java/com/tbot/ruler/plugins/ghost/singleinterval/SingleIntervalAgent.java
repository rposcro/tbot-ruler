package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.SubjectState;
import lombok.Getter;

@Getter
public class SingleIntervalAgent {

    private SubjectState<OnOffState> currentState;

    public SingleIntervalAgent(String actuatorUuid, boolean defaultState) {
        this.currentState = SubjectState.<OnOffState>builder()
                .subjectUuid(actuatorUuid)
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
