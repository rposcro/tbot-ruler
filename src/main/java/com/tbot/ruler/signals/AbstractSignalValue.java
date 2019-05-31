package com.tbot.ruler.signals;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString()
public abstract class AbstractSignalValue implements SignalValue {
    private SignalValueType signalValueType;
}
