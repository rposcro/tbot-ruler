package com.tbot.ruler.subjects.thing;

import com.tbot.ruler.broker.payload.OnOffState;
import lombok.Getter;

@Getter
public class RulerThingAgent {

    private boolean activated = true;

    public void setActivated(OnOffState onOffState) {
        this.activated = onOffState.isOn();
    }
}
