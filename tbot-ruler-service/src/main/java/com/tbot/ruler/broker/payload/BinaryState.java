package com.tbot.ruler.broker.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor()
public class BinaryState {

    public static final BinaryState ON = new BinaryState(true);
    public static final BinaryState OFF = new BinaryState(false);

    private boolean on;

    @JsonCreator
    public static BinaryState of(@JsonProperty("on") boolean on) {
        return on ? ON : OFF;
    }

    public BinaryState negate() {
        return isOn() ? OFF : ON;
    }

    @Override
    public String toString() {
        return String.format("OnOffState { on: %s }", on);
    }
}
