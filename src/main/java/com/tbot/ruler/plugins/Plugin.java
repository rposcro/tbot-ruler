package com.tbot.ruler.plugins;

import com.tbot.ruler.exceptions.CriticalException;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.subjects.Thing;

public interface Plugin extends Subject {

    Thing startUpThing(ThingEntity thingEntity);

    default void destroyThing(Thing thing) {
        throw new CriticalException("Method not implemented by the plugin");
    }
}
