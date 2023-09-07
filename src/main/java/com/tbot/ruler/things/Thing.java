package com.tbot.ruler.things;

import java.util.List;

public interface Thing extends TaskBasedItem {

    String getId();
    String getName();
    String getDescription();
    List<? extends Emitter> getEmitters();
    List<? extends Collector> getCollectors();
    List<? extends Actuator> getActuators();
}
