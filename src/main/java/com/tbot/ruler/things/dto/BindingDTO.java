package com.tbot.ruler.things.dto;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.CollectorId;
import com.tbot.ruler.things.EmitterId;
import lombok.Data;

import java.util.List;

@Data
public class BindingDTO {

    private ApplianceId applianceId;
    private ActuatorId actuatorId;
    private List<EmitterId> emitterIds;
    private List<CollectorId> collectorIds;
}
