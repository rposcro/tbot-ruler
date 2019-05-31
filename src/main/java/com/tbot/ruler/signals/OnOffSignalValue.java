package com.tbot.ruler.signals;

import lombok.ToString;

@ToString(callSuper = true)
public class OnOffSignalValue extends AbstractSignalValue {

    public static final OnOffSignalValue ON_SIGNAL_VALUE = new OnOffSignalValue(true);
    public static final OnOffSignalValue OFF_SIGNAL_VALUE = new OnOffSignalValue(false);

    private boolean onFlag;

    public OnOffSignalValue(boolean value) {
        super(SignalValueType.OnOff);
        this.onFlag = value;
    }

    public OnOffSignalValue(String value) {
        this(decodeValue(value));
    }
    
    private static boolean decodeValue(String value) {
        return "on".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }
    
    public boolean isOnSignal() {
        return onFlag;
    }

    public boolean isOffSignal() {
        return !onFlag;
    }

    static SignalValue parseSignalValue(String signalValue) {
        boolean value = "true".equalsIgnoreCase(signalValue) || "on".equalsIgnoreCase(signalValue);
        return new OnOffSignalValue(value);
    }
}
