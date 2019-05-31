package com.tbot.ruler.signals;

import lombok.ToString;

@ToString(callSuper = true)
public class BooleanSignalValue extends AbstractSignalValue {
    
    private boolean value;
    
    public BooleanSignalValue(String value) {
        this(Boolean.parseBoolean(value));
    }
    
    public BooleanSignalValue(boolean value) {
        super(SignalValueType.Boolean);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    static SignalValue parseSignalValue(String signalValue) {
        return new BooleanSignalValue(Boolean.parseBoolean(signalValue));
    }
}
