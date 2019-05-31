package com.tbot.ruler.things;

import java.util.List;

public interface Thing {

    public ThingId getId();
    public ThingMetadata getMetadata();
    public List<? extends Emitter> getEmitters();
    public List<? extends Collector> getCollectors();
    public List<? extends Actuator> getActuators();
}
