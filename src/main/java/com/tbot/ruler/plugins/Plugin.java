package com.tbot.ruler.plugins;

import com.tbot.ruler.things.TaskBasedItem;
import com.tbot.ruler.things.Thing;

import java.util.List;

public interface Plugin extends TaskBasedItem {

    String getUuid();
    String getName();

    List<? extends Thing> getThings();
}
