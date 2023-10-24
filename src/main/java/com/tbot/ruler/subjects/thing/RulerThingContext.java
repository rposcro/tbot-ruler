package com.tbot.ruler.subjects.thing;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RulerThingContext {

    private final String thingUuid;
    private final String thingName;
    private final String thingDescription;
    private final JsonNode thingConfiguration;
    private final RulerThingAgent rulerThingAgent;
}
