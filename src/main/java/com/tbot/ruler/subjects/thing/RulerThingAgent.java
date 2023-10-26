package com.tbot.ruler.subjects.thing;

import com.tbot.ruler.broker.payload.OnOffState;
import lombok.Getter;

@Getter
public class RulerThingAgent {

    private boolean silenced = false;

    public void setSilenced(OnOffState onOffState) {
        this.silenced = onOffState.isOn();
    }
}
