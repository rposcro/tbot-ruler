package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SingleIntervalAgent {

    private final SubjectStateService subjectStateService;
    private final ActuatorState<BinaryState> currentState;

    @Builder
    public SingleIntervalAgent(String actuatorUuid, boolean defaultState, SubjectStateService subjectStateService) {
        this.subjectStateService = subjectStateService;
        this.currentState = subjectStateService.recoverActuatorState(actuatorUuid, BinaryState.class)
            .orElse(ActuatorState.<BinaryState>builder()
                .actuatorUuid(actuatorUuid)
                .payload(BinaryState.of(defaultState))
                .build());
    }

    public boolean isActivated() {
        return currentState.getPayload().isOn();
    }

    public void setActivated(boolean active) {
        currentState.updatePayload(BinaryState.of(active));
        subjectStateService.persistState(currentState);
    }
}
