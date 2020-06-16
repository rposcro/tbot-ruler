package com.tbot.ruler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor()
public class OnOffState {

    public static final OnOffState STATE_ON = new OnOffState(true);
    public static final OnOffState STATE_OFF = new OnOffState(false);

    public static OnOffState of(boolean state) {
        return state ? STATE_ON : STATE_OFF;
    }

    private boolean on;
}
