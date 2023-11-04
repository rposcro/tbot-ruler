package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.broker.DefaultMessagePublisher;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.thing.RulerThing;
import com.tbot.ruler.subjects.thing.RulerThingAgent;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThingFactoryComponent {

    @Autowired
    private DefaultMessagePublisher defaultMessageQueue;

    @Autowired
    private SubjectStateService subjectStateService;

    public RulerThing buildThing(ThingEntity thingEntity) {
        RulerThingAgent thingAgent = new RulerThingAgent();
        RulerThingContext thingContext = RulerThingContext.builder()
                .thingUuid(thingEntity.getThingUuid())
                .thingName(thingEntity.getName())
                .thingDescription(thingEntity.getDescription())
                .thingConfiguration(thingEntity.getConfiguration())
                .rulerThingAgent(thingAgent)
                .messagePublisher(buildPublisher(thingAgent, thingEntity.getThingUuid()))
                .subjectStateService(subjectStateService)
                .build();
        return RulerThing.builder()
                .rulerThingContext(thingContext)
                .build();
    }

    private MessagePublisher buildPublisher(RulerThingAgent thingAgent, String thingUuid) {
        return message -> {
             if (!thingAgent.isOnMute()) {
                 defaultMessageQueue.publishMessage(message);
             } else {
                 log.debug("Silenced message from actuator {} of thing {}", message.getSenderId(), thingUuid);
             }
        };
    }
}
