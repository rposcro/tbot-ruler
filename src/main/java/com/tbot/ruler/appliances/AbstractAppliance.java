package com.tbot.ruler.appliances;

import com.tbot.ruler.things.ApplianceId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAppliance implements Appliance {

    @NonNull private ApplianceId id;
    @NonNull private String name;
    private String description;
}
