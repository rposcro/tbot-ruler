package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingsManipulator {

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    public void removeThing(ThingEntity thingEntity) {
        assertConsistency(thingEntity);
        thingsRepository.findByUuid(thingEntity.getThingUuid()).ifPresent(thingsRepository::delete);
    }

    private void assertConsistency(ThingEntity thingEntity) {
        if (actuatorsRepository.actuatorsForThingExist(thingEntity.getThingId())) {
            throw new LifecycleException("Cannot remove thing %s, actuator(s) exist(s)!", thingEntity.getThingUuid());
        }
    }
}
