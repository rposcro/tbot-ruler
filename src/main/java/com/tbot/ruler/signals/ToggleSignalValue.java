package com.tbot.ruler.signals;

import lombok.ToString;

@ToString(callSuper = true)
public class ToggleSignalValue extends AbstractSignalValue {

    public ToggleSignalValue() {
        super(SignalValueType.Toggle);
    }

    static SignalValue parseSignalValue(String signalValue) {
        return new ToggleSignalValue();
    }
}
