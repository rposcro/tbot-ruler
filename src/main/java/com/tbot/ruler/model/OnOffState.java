package com.tbot.ruler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor()
public class OnOffState {

    public static final OnOffState STATE_ON = new OnOffState(true);
    public static final OnOffState STATE_OFF = new OnOffState(false);

    private boolean on;

    @JsonCreator
    public static OnOffState of(@JsonProperty("on") boolean on) {
        return on ? STATE_ON : STATE_OFF;
    }
}
