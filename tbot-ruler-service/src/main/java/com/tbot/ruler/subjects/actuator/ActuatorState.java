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

    public static <T> ActuatorState<T> of(String actuatorUuid, T payload) {
        return ActuatorState.<T>builder()
                .actuatorUuid(actuatorUuid)
                .payload(payload)
                .build();
    }
}
