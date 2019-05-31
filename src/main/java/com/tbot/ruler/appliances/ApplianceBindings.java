package com.tbot.ruler.appliances;

import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.CollectorId;
import com.tbot.ruler.things.EmitterId;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class ApplianceBindings {

    private ApplianceId applianceId;
    private ActuatorId actuatorId;
    @Builder.Default
    private List<EmitterId> emitterIds = Collections.emptyList();
    @Builder.Default
    private List<CollectorId> collectorIds = Collections.emptyList();
}
