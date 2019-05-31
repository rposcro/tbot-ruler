package com.tbot.ruler.things;

import com.tbot.ruler.signals.SignalValueType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EmitterMetadata {
    private EmitterId id;
    private SignalValueType emittedSignalType;
    private String name;
    private String description;
}
