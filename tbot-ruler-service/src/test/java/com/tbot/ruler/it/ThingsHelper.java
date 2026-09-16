package com.tbot.ruler.it;

import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ThingsHelper {

    @Autowired
    private ThingsRepository thingsRepository;

    public ThingEntity insertThing() {
        ThingEntity thingEntity = newThingEntity();
        return thingsRepository.save(thingEntity);
    }

    public ThingEntity newThingEntity() {
        return ThingEntity.builder()
                .thingUuid("thng-" + UUID.randomUUID())
                .name("Thing Name")
                .build();
    }
}
