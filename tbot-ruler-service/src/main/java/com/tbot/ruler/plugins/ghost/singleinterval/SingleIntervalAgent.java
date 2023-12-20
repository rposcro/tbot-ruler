package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SingleIntervalAgent {

    private final SubjectStateService subjectStateService;
    private final ActuatorState<OnOffState> currentState;

    @Builder
    public SingleIntervalAgent(String actuatorUuid, boolean defaultState, SubjectStateService subjectStateService) {
        this.subjectStateService = subjectStateService;

        ActuatorState<OnOffState> state = subjectStateService.recoverActuatorState(actuatorUuid, OnOffState.class);
        if (state == null) {
            state = ActuatorState.<OnOffState>builder()
                    .actuatorUuid(actuatorUuid)
                    .payload(OnOffState.of(defaultState))
                    .build();

        }
        this.currentState = state;
    }

    public boolean isActivated() {
        return currentState.getPayload().isOn();
    }

    public void setActivated(boolean active) {
        currentState.updatePayload(OnOffState.of(active));
        subjectStateService.persistState(currentState);
    }
}
