package com.tbot.ruler.subjects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SubjectState<T> {

    String subjectUuid;
    T payload;

    public void updatePayload(T payload) {
        this.payload = payload;
    }
}
