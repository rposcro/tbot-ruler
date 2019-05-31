package com.tbot.ruler.signals;

import com.tbot.ruler.things.EmitterId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class EmitterSignal {

    private SignalValue signalValue;
    private EmitterId emitterId;
}
