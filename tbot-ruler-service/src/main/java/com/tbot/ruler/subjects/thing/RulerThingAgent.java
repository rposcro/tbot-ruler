package com.tbot.ruler.subjects.thing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class RulerThingAgent {

    @Getter
    @Setter
    private boolean onMute = false;

    @Setter(AccessLevel.PROTECTED)
    private RulerThing thing;
}
