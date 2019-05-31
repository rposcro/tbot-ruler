package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class SunEventEmitter implements Emitter {

    private EmitterId id;
    private EmitterMetadata metadata;
}
