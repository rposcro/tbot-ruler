package com.tbot.ruler.things;

import java.util.List;

public interface Thing extends TaskBasedItem {

    String getUuid();
    String getName();
    String getDescription();
    List<? extends Actuator> getActuators();
}
