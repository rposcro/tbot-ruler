package com.tbot.ruler.signals;

import java.util.function.Function;

public enum SignalValueType {

    OnOff(OnOffSignalValue::parseSignalValue),
    Boolean(BooleanSignalValue::parseSignalValue),
    Toggle(ToggleSignalValue::parseSignalValue)
    ;

    private Function<String, SignalValue> builder;

    private SignalValueType(Function<String, SignalValue> builder) {
        this.builder = builder;
    }

    Function<String, SignalValue> signalBuilder() {
        return builder;
    }
}
