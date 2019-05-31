package com.tbot.ruler.things;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ActuatorId {

    private final String value;

    public ActuatorId(String value) {
        this.value = value;
    }
}
