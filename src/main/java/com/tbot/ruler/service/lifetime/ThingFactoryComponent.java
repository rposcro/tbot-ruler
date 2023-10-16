package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.subjects.thing.Thing;
import com.tbot.ruler.subjects.thing.RulerThing;
import com.tbot.ruler.subjects.thing.RulerThingAgent;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThingFactoryComponent {

    public Thing buildThing(ThingEntity thingEntity) {
        RulerThingAgent thingAgent = new RulerThingAgent();
        RulerThingContext thingContext = RulerThingContext.builder()
                .thingUuid(thingEntity.getThingUuid())
                .thingName(thingEntity.getName())
                .thingDescription(thingEntity.getDescription())
                .thingConfiguration(thingEntity.getConfiguration())
                .rulerThingAgent(thingAgent)
                .build();
        return RulerThing.builder()
                .rulerThingContext(thingContext)
                .build();
    }
}
