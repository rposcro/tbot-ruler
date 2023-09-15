package com.tbot.ruler.things;

import java.util.List;

public interface Thing extends TaskBasedItem {

    String getId();
    String getName();
    String getDescription();
    List<? extends Actuator> getActuators();
}
