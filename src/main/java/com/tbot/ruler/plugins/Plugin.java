package com.tbot.ruler.plugins;

import com.tbot.ruler.things.Thing;

import java.util.List;

public interface Plugin {

    String getUuid();
    String getName();

    List<? extends Thing> getThings();
}
