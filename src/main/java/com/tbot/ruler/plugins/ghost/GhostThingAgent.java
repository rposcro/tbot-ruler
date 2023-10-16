package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.broker.payload.OnOffState;
import lombok.Getter;

@Getter
public class GhostThingAgent {

    private boolean activated = true;

    public void setActivated(OnOffState onOffState) {
        this.activated = onOffState.isOn();
    }
}
