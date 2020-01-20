package com.tbot.ruler.appliances.state;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OnOffState implements State {

    public static final OnOffState STATE_ON = new OnOffState(true);
    public static final OnOffState STATE_OFF = new OnOffState(false);

    public static OnOffState of(boolean state) {
        return state ? STATE_ON : STATE_OFF;
    }

    private boolean on;

    public OnOffState invert() {
        return on ? STATE_OFF : STATE_ON;
    }
}
