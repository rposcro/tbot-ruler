package com.tbot.ruler.appliances;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ApplianceId {

    private final String value;

    public ApplianceId(String value) {
        this.value = value;
    }
}
