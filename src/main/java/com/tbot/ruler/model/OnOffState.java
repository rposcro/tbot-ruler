package com.tbot.ruler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor()
public class OnOffState {

    public static final OnOffState STATE_ON = new OnOffState(true);
    public static final OnOffState STATE_OFF = new OnOffState(false);

    private boolean on;

    @JsonCreator
    public static OnOffState of(boolean on) {
        return on ? STATE_ON : STATE_OFF;
    }
}
