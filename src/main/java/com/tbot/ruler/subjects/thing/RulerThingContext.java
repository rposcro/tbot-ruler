package com.tbot.ruler.subjects.thing;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.service.things.SubjectStateService;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class RulerThingContext {

    @NonNull
    private final String thingUuid;
    @NonNull
    private final String thingName;
    private final String thingDescription;
    private final JsonNode thingConfiguration;

    @NonNull
    private final RulerThingAgent rulerThingAgent;
    @NonNull
    private final MessagePublisher messagePublisher;
    @NonNull
    private final SubjectStateService subjectStateService;
}
