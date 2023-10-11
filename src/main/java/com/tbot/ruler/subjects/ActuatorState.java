package com.tbot.ruler.subjects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class ActuatorState<T> {

    String actuatorUuid;
    T payload;

    public void updatePayload(T payload) {
        this.payload = payload;
    }
}
