package com.tbot.ruler.appliances;

import com.tbot.ruler.things.RecipientId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
public class ApplianceId extends RecipientId {

    public ApplianceId(String value) {
        super(value);
    }
}
