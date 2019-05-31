package com.tbot.ruler.signals;

public interface SignalValue {
    public SignalValueType getSignalValueType();

    public static SignalValue parse(String signalType, String signalValueAsString) {
        SignalValueType type = SignalValueType.valueOf(signalType);
        SignalValue signalValue = type.signalBuilder().apply(signalValueAsString);
        return signalValue;
    }
}
