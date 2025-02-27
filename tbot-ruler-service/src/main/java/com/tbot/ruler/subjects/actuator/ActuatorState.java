package com.tbot.ruler.subjects.actuator;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class ActuatorState<T> {

    private String actuatorUuid;
    private T payload;

    public void updatePayload(T payload) {
        this.payload = payload;
    }
}
