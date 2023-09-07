package com.tbot.ruler.plugins.ghost.singleinterval;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleIntervalStateAgent {

    private boolean active;

    public SingleIntervalStateAgent() {
        this.active = true;
    }
}
