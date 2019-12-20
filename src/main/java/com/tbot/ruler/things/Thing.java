package com.tbot.ruler.things;

import java.util.List;

public interface Thing extends TaskBased {

    ItemId getId();
    String getName();
    String getDescription();
    List<? extends Emitter> getEmitters();
    List<? extends Collector> getCollectors();
}
