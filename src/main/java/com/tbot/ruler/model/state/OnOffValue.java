package com.tbot.ruler.model.state;

import com.tbot.ruler.signals.OnOffSignalValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OnOffValue implements StateValue {

    public static final OnOffValue STATE_ON = new OnOffValue(true);
    public static final OnOffValue STATE_OFF = new OnOffValue(false);

    public static OnOffValue of(OnOffSignalValue signalValue) {
        return signalValue.isOnSignal() ? STATE_ON : STATE_OFF;
    }

    public static OnOffValue of(boolean state) {
        return state ? STATE_ON : STATE_OFF;
    }

    private boolean on;

    public OnOffValue invert() {
        return on ? STATE_OFF : STATE_ON;
    }
}
