package com.tbot.ruler.signals;

import com.tbot.ruler.appliances.ApplianceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ApplianceSignal {

    private SignalValue signalValue;
    private ApplianceId applianceId;
}
