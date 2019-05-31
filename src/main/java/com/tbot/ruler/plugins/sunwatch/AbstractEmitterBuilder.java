package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.signals.OnOffSignalValue;
import com.tbot.ruler.signals.SignalValueType;
import com.tbot.ruler.things.EmitterMetadata;
import com.tbot.ruler.things.dto.EmitterDTO;

public abstract class AbstractEmitterBuilder {

    protected EmitterSignal emitterSignal(EmitterDTO emitterDTO, String paramName) {
        return new EmitterSignal(
            new OnOffSignalValue(emitterDTO.getConfig().get(paramName)),
            emitterDTO.getId());
    }

    protected Long plusMinutes(EmitterDTO emitterDTO, String paramName) {
        return Long.parseLong(
            emitterDTO.getConfig().getOrDefault(paramName, "0"));
    }

    protected EmitterMetadata emitterMetadata(EmitterDTO emitterDTO) {
        return EmitterMetadata.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getName())
            .emittedSignalType(SignalValueType.OnOff)
            .build();
    }
}
