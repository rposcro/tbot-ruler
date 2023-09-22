package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.SubjectState;
import lombok.Getter;

@Getter
public class SingleIntervalActuatorState implements SubjectState {

    protected String subjectUuid;
    protected OnOffState payload;

    public SingleIntervalActuatorState(String actuatorUuid, boolean defaultState) {
        this.subjectUuid = actuatorUuid;
        this.payload = OnOffState.of(defaultState);
    }

    public boolean isActive() {
        return payload.isOn();
    }

    public void setActive(boolean active) {
        payload = OnOffState.of(active);
    }
}
